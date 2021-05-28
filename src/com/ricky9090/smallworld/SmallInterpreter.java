package com.ricky9090.smallworld;

import com.ricky9090.smallworld.display.ButtonListener;
import com.ricky9090.smallworld.display.IScreen;
import com.ricky9090.smallworld.display.ListListener;
import com.ricky9090.smallworld.policy.ScreenPolicy;
import com.ricky9090.smallworld.obj.SmallByteArray;
import com.ricky9090.smallworld.obj.SmallInt;
import com.ricky9090.smallworld.obj.SmallJavaObject;
import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.task.ITaskManager;
import com.ricky9090.smallworld.policy.TaskPolicy;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.util.List;
import java.util.jar.*;
import java.util.*;

/**
 * I do all of the heavy lifting, including
 * (currently) the loading and saving of
 * an image to file.
 * <p>
 * I hold references to specific objects in the system:
 * nil, true, false, Array, Block, Context, Integer and
 * the numbers 0-9.
 * <p>
 * From these references, you can get to Class, which holds
 * references to all the classes in the system in Class#classes.
 */
public class SmallInterpreter {

    // version
    public static final int imageFormatVersion = 1;

    // --- global constants begin ---
    public SmallObject nilObject;
    public SmallObject trueObject;
    public SmallObject falseObject;

    public SmallInt[] smallIntCache;

    public SmallObject ArrayClass;
    public SmallObject BlockClass;
    public SmallObject ContextClass;
    public SmallObject IntegerClass;
    // --- global constants end ---

    IScreen screen;
    ITaskManager taskManager;

    public SmallInterpreter() {
        screen = ScreenPolicy.provideScreen();
        taskManager = TaskPolicy.provideTaskManager(this);
    }

    // Load and save image
    public boolean loadImageFromInputStream(InputStream name) {
        try {
            List<SmallObject> objectList = new ArrayList<>();
            List<int[]> objectDataList = new ArrayList<>();

            // Read in input into list
            // for each line in list, create SmallObject of appropriate class in obj
            DataInputStream r = new DataInputStream(new BufferedInputStream(name));
            try {
                int version = r.readInt();
                if (version != imageFormatVersion) {
                    String msg = "Incorrect Image Version:\nI was expecting " + imageFormatVersion + " but got " + version + "  \n";
                    screen.showToast(msg);
                }
                while (true) {
                    int si = r.readInt();
                    int[] qw = new int[si];
                    qw[SmallConst.OBJ.INDEX_OBJ_LENGTH] = si;
                    qw[SmallConst.OBJ.INDEX_OBJ_TYPE] = r.readInt();

                    int objLen = qw[SmallConst.OBJ.INDEX_OBJ_LENGTH];
                    int objType = qw[SmallConst.OBJ.INDEX_OBJ_TYPE];

                    if (objType == SmallConst.OBJ.TYPE_SMALL_INT) {
                        for (int i = 2; i < objLen; i++) {
                            qw[i] = r.readInt();
                        }
                        objectList.add(new SmallInt());
                    } else if (objType == SmallConst.OBJ.TYPE_SMALL_OBJECT) {
                        for (int i = 2; i < objLen; i++) {
                            qw[i] = r.readInt();
                        }
                        objectList.add(new SmallObject());
                    } else if (objType == SmallConst.OBJ.TYPE_SMALL_BYTE_ARRAY) {
                        qw[SmallConst.OBJ.INDEX_OBJ_CLASS] = r.readInt(); // class
                        qw[SmallConst.OBJ.INDEX_OBJ_DATA_LENGTH] = r.readInt(); // datasize

                        int objDataLen = qw[SmallConst.OBJ.INDEX_OBJ_DATA_LENGTH];

                        for (int i = 4; i < 4 + objDataLen; i++) {
                            qw[i] = r.readInt();
                        }
                        for (int i = 4 + objDataLen; i < objLen; i++) {
                            qw[i] = r.readByte();
                        }
                        objectList.add(new SmallByteArray());
                    }

                    objectDataList.add(qw);
                }
            } catch (IOException e) {
                if (e instanceof EOFException) {
                    System.out.println("Done reading into list");
                } else {
                    System.err.println("Error loading image !");
                }
            }

            // using list, fill in fields in SmallObjects in obj
            for (int it = 0; it < objectDataList.size(); it++) {
                SmallObject target = objectList.get(it);
                int[] rawData = objectDataList.get(it);

                // Determine values
                int objLength = rawData[SmallConst.OBJ.INDEX_OBJ_LENGTH];
                int objType = rawData[SmallConst.OBJ.INDEX_OBJ_TYPE];
                int objClass = rawData[SmallConst.OBJ.INDEX_OBJ_CLASS];
                int objDataLength = rawData[SmallConst.OBJ.INDEX_OBJ_DATA_LENGTH];

                int[] objData = new int[objDataLength];
                if (objDataLength >= 0) {
                    System.arraycopy(rawData, 4, objData, 0, objDataLength);
                }

                // For SmallInt
                if (objType == SmallConst.OBJ.TYPE_SMALL_INT) {
                    ((SmallInt) target).value = rawData[objLength - 1];
                }

                // For SmallByteArray
                if (objType == SmallConst.OBJ.TYPE_SMALL_BYTE_ARRAY) {
                    byte[] byteArrayValues = new byte[objLength - 4 - objDataLength];
                    for (int i = 4 + objDataLength; i < objLength; i++) {
                        byteArrayValues[i - 4 - objDataLength] = (byte) rawData[i];
                    }

                    ((SmallByteArray) target).values = byteArrayValues;
                }

                // Add values
                target.objClass = objectList.get(objClass);
                target.data = new SmallObject[objDataLength];
                for (int i = 0; i < objDataLength; i++) {
                    target.data[i] = objectList.get(objData[i]);
                }

            }
            System.out.println("Done initialising SmallObjects");

            // set up constants
            nilObject = objectList.get(0);
            trueObject = objectList.get(1);
            falseObject = objectList.get(2);
            ArrayClass = objectList.get(3);
            BlockClass = objectList.get(4);
            ContextClass = objectList.get(5);
            IntegerClass = objectList.get(6);

            smallIntCache = new SmallInt[10];
            smallIntCache[0] = (SmallInt) objectList.get(7);
            smallIntCache[1] = (SmallInt) objectList.get(8);
            smallIntCache[2] = (SmallInt) objectList.get(9);
            smallIntCache[3] = (SmallInt) objectList.get(10);
            smallIntCache[4] = (SmallInt) objectList.get(11);
            smallIntCache[5] = (SmallInt) objectList.get(12);
            smallIntCache[6] = (SmallInt) objectList.get(13);
            smallIntCache[7] = (SmallInt) objectList.get(14);
            smallIntCache[8] = (SmallInt) objectList.get(15);
            smallIntCache[9] = (SmallInt) objectList.get(16);
            System.out.println("Done loading system");

            System.out.println("Starting taskManager");
            taskManager.launch();


            return true;
        } catch (Exception e) {
            String msg = "IO Exception: " + e.toString();
            screen.showToast(msg);
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveImageToOutputStream(OutputStream name) {
        LinkedHashSet<SmallObject> set = new LinkedHashSet<>();
        set.add(nilObject);
        set.add(trueObject);
        set.add(falseObject);
        set.add(ArrayClass);
        set.add(BlockClass);
        set.add(ContextClass);
        set.add(IntegerClass);
        set.addAll(Arrays.asList(smallIntCache));
        while (true) {
            java.util.List<SmallObject> newList = new ArrayList<>();
            for (SmallObject o : set) {
                newList.add(o.objClass);
                newList.addAll(Arrays.asList(o.data));
            }
            int s1 = set.size();
            set.addAll(newList);
            if ((set.size() - s1) == 0) {
                break;
            }
        }

        ArrayList<SmallObject> fulllist = new ArrayList<>(set);

        // At this point we should have a flat object memory in list
        // what size is it?
        // System.out.print("Total Object Size:  ");
        // System.out.print(list.size());
        // System.out.println();

        // Dump SmallJavaObjects
        ArrayList<SmallObject> list = new ArrayList<>();
        for (SmallObject o : fulllist) {
            if (!(o instanceof SmallJavaObject)) {
                list.add(o);
            }
        }

        // Give each remaining object an id
        for (int i = 0; i < list.size(); i++) {
            list.get(i).id = i;
        }

        // File out...
        DataOutputStream im = new DataOutputStream(new BufferedOutputStream(name));
        try {
            im.writeInt(imageFormatVersion);

            for (SmallObject o : list) {
                if (o instanceof SmallInt) {
                    // Write size of record
                    int s = 1 /* length */ + 1 /* type */ + 1 /* class */ + 1 /* size of data */ + o.data.length + 1;
                    im.writeInt(s);
                    im.writeInt(0); // SmallInt
                    im.writeInt(o.objClass.id); // Class
                    im.writeInt(o.data.length); // Length of data
                    for (SmallObject o2 : o.data) {
                        // Fix references to SmallJavaObjects, which can't be serialised yet
                        if (o2 instanceof SmallJavaObject) {
                            im.writeInt(nilObject.id);
                        } else {
                            im.writeInt(o2.id);
                        }
                    }
                    im.writeInt(((SmallInt) o).value);
                } else if (o instanceof SmallByteArray) {
                    // Write size of record
                    int s = 1 /* length */ + 1 /* type */ + 1 /* class */ + 1 /* size of data */ + o.data.length + ((SmallByteArray) o).values.length;
                    im.writeInt(s);
                    im.writeInt(1); // SmallByteArray
                    im.writeInt(o.objClass.id); // Class
                    im.writeInt(o.data.length); // Length of data
                    for (SmallObject o2 : o.data) {
                        // Fix references to SmallJavaObjects, which can't be serialised yet
                        if (o2 instanceof SmallJavaObject) {
                            im.writeInt(nilObject.id);
                        } else {
                            im.writeInt(o2.id);
                        }

                    }
                    for (byte b : ((SmallByteArray) o).values) {
                        im.writeByte(b);
                    }
                } else if (o instanceof SmallJavaObject) {
                    // Do nothing - SmallJavaObjects cannot be serialised at this stage
                    // this is a placeholder for if we change our minds
                } else {
                    // Write size of record
                    int s = 1 /* length */ + 1 /* type */ + 1 /* class */ + 1 /* size of data */ + o.data.length;
                    im.writeInt(s);
                    im.writeInt(2); // SmallObject
                    im.writeInt(o.objClass.id); // Class
                    im.writeInt(o.data.length); // Length of data
                    for (SmallObject o2 : o.data) {
                        // Fix references to SmallJavaObjects, which can't be serialised yet
                        if (o2 instanceof SmallJavaObject) {
                            im.writeInt(nilObject.id);
                        } else {
                            im.writeInt(o2.id);
                        }

                    }
                }
            }
            im.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    // create a new small integer
    SmallInt newInteger(int val) {
        if ((val >= 0) && (val < 10)) {
            return smallIntCache[val];
        } else {
            return new SmallInt(IntegerClass, val);
        }
    }

    private SmallObject methodLookup(SmallObject receiver,
                                     SmallByteArray messageSelector, SmallObject context,
                                     SmallObject arguments)
            throws SmallException {
        String name = messageSelector.toString();
        SmallObject cls;
        for (cls = receiver; cls != nilObject; cls = cls.data[1]) {
            SmallObject dict = cls.data[2]; // dictionary in class
            for (int i = 0; i < dict.data.length; i++) {
                SmallObject aMethod = dict.data[i];
                if (name.equals(aMethod.data[0].toString())) {
                    return aMethod;
                }
            }
        }
        // try once to handle method in Smalltalk before giving up
        if (name.equals("doesNotUnderstand:"))
            throw new SmallException("Unrecognized message selector: " +
                    messageSelector, context);
        SmallObject[] newArgs = new SmallObject[2];
        newArgs[0] = arguments.data[0]; // same receiver
        newArgs[1] = new SmallObject(ArrayClass, 2);
        newArgs[1].data[0] = messageSelector;
        newArgs[1].data[1] = arguments.duplicate();
        arguments.data = newArgs;
        return methodLookup(receiver,
                new SmallByteArray(messageSelector.objClass, "doesNotUnderstand:"),
                context, arguments);
    }

    SmallObject buildContext
            (SmallObject oldContext, SmallObject arguments, SmallObject method) {
        SmallObject context = new SmallObject(ContextClass, 7);
        context.data[0] = method;
        context.data[1] = arguments;
        // allocate temporaries
        int max = ((SmallInt) (method.data[4])).value;
        if (max > 0) {
            context.data[2] = new SmallObject(ArrayClass, max);
            while (max > 0) // iniailize to nil
                context.data[2].data[--max] = nilObject;
        }
        // allocate stack
        max = ((SmallInt) (method.data[3])).value;
        context.data[3] = new SmallObject(ArrayClass, max);
        context.data[4] = smallIntCache[0]; // byte pointer
        context.data[5] = smallIntCache[0]; // stacktop
        context.data[6] = oldContext;
        return context;
    }

    // execution method
    SmallObject execute(SmallObject context,
                        final Thread myThread, final Thread parentThread) throws SmallException {
        SmallObject[] selectorCache = new SmallObject[197];
        SmallObject[] classCache = new SmallObject[197];
        SmallObject[] methodCache = new SmallObject[197];
        int lookup = 0;
        int cached = 0;

        SmallObject[] contextData = context.data;

        outerLoop:
        while (true) {
            //System.out.println("enter outer loop");
            SmallObject method = contextData[0]; // method in context
            byte[] code = ((SmallByteArray) method.data[1]).values; // code pointer
            int bytePointer = ((SmallInt) contextData[4]).value;
            SmallObject[] stack = contextData[3].data;
            int stackTop = ((SmallInt) contextData[5]).value;
            SmallObject returnedValue = null;
            SmallObject temp;
            SmallObject[] tempArray;

            // everything else can be null for now
            SmallObject[] temporaries = null;
            SmallObject[] instanceVariables = null;
            SmallObject arguments = null;
            SmallObject[] literals = null;

            innerLoop:
            while (true) {
                int high = code[bytePointer++];
                int low = high & 0x0F;
                high = (high >>= 4) & 0x0F;
                if (high == 0) {
                    high = low;
                    // convert to positive int
                    low = (int) code[bytePointer++] & 0x0FF;
                }

                //System.out.println("enter inner loop code: " + high);
                switch (high) {

                    case 1: // PushInstance
                        if (arguments == null) {
                            arguments = contextData[1];
                        }
                        if (instanceVariables == null) {
                            instanceVariables = arguments.data[0].data;
                        }
                        stack[stackTop++] = instanceVariables[low];
                        break;

                    case 2: // PushArgument
                        if (arguments == null) {
                            arguments = contextData[1];
                        }
                        stack[stackTop++] = arguments.data[low];
                        break;

                    case 3: // PushTemporary
                        if (temporaries == null) {
                            temporaries = contextData[2].data;
                        }
                        stack[stackTop++] = temporaries[low];
                        break;

                    case 4: // PushLiteral
                        if (literals == null) {
                            literals = method.data[2].data;
                        }
                        stack[stackTop++] = literals[low];
                        break;

                    case 5: // PushConstant
                        switch (low) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                                stack[stackTop++] = smallIntCache[low];
                                break;
                            case 10:
                                stack[stackTop++] = nilObject;
                                break;
                            case 11:
                                stack[stackTop++] = trueObject;
                                break;
                            case 12:
                                stack[stackTop++] = falseObject;
                                break;
                            default:
                                throw new SmallException("Unknown constant " + low, context);
                        }
                        break;

                    case 12: // PushBlock
                        // low is argument location
                        // next byte is goto value
                        high = (int) code[bytePointer++] & 0x0FF;
                        returnedValue = new SmallObject(BlockClass, 10);
                        tempArray = returnedValue.data;
                        tempArray[0] = contextData[0]; // share method
                        tempArray[1] = contextData[1]; // share arguments
                        tempArray[2] = contextData[2]; // share temporaries
                        tempArray[3] = contextData[3]; // stack (later replaced)
                        tempArray[4] = newInteger(bytePointer); // current byte pointer
                        tempArray[5] = smallIntCache[0]; // stacktop
                        tempArray[6] = contextData[6]; // previous context
                        tempArray[7] = newInteger(low); // argument location
                        tempArray[8] = context; // creating context
                        tempArray[9] = newInteger(bytePointer); // current byte pointer
                        stack[stackTop++] = returnedValue;
                        bytePointer = high;
                        break;

                    case 14: // PushClassVariable
                        if (arguments == null) {
                            arguments = contextData[1];
                        }
                        if (instanceVariables == null) {
                            instanceVariables = arguments.data[0].data;
                        }
                        stack[stackTop++] = arguments.data[0].objClass.data[low + 5];
                        break;

                    case 6: // AssignInstance
                        if (arguments == null) {
                            arguments = contextData[1];
                        }
                        if (instanceVariables == null) {
                            instanceVariables = arguments.data[0].data;
                        }
                        // leave result on stack
                        instanceVariables[low] = stack[stackTop - 1];
                        break;

                    case 7: // AssignTemporary
                        if (temporaries == null) {
                            temporaries = contextData[2].data;
                        }
                        temporaries[low] = stack[stackTop - 1];
                        break;

                    case 8: // MarkArguments
                        SmallObject newArguments = new SmallObject(ArrayClass, low);
                        tempArray = newArguments.data; // direct access to array
                        while (low > 0) {
                            tempArray[--low] = stack[--stackTop];
                        }
                        stack[stackTop++] = newArguments;
                        break;

                    case 9: // SendMessage
                        // save old context
                        arguments = stack[--stackTop];
                        // expand newInteger in line
                        //contextData[5] = newInteger(stackTop);
                        contextData[5] = (stackTop < 10) ? smallIntCache[stackTop] : new SmallInt(IntegerClass, stackTop);
                        //contextData[4] = newInteger(bytePointer);
                        contextData[4] = (bytePointer < 10) ? smallIntCache[bytePointer] : new SmallInt(IntegerClass, bytePointer);
                        // now build new context
                        if (literals == null) {
                            literals = method.data[2].data;
                        }
                        returnedValue = literals[low]; // message selector
                        // System.out.println("Sending " + returnedValue);
                        // System.out.println("Arguments " + arguments);
                        // System.out.println("Arguments receiver " + arguments.data[0]);
                        // System.out.println("Arguments class " + arguments.data[0].objClass);
                        high = Math.abs(arguments.data[0].objClass.hashCode() + returnedValue.hashCode()) % 197;
                        if ((selectorCache[high] != null) &&
                                (selectorCache[high] == returnedValue) &&
                                (classCache[high] == arguments.data[0].objClass)) {
                            method = methodCache[high];
                            cached++;
                        } else {
                            method = methodLookup(arguments.data[0].objClass, (SmallByteArray) literals[low], context, arguments);
                            lookup++;
                            selectorCache[high] = returnedValue;
                            classCache[high] = arguments.data[0].objClass;
                            methodCache[high] = method;
                        }
                        context = buildContext(context, arguments, method);
                        contextData = context.data;
                        // load information from context
                        continue outerLoop;

                    case 10: // SendUnary
                        if (low == 0) { // isNil
                            SmallObject arg = stack[--stackTop];
                            stack[stackTop++] = (arg == nilObject) ? trueObject : falseObject;
                        } else if (low == 1) { // notNil
                            SmallObject arg = stack[--stackTop];
                            stack[stackTop++] = (arg != nilObject) ? trueObject : falseObject;
                        } else {
                            throw new SmallException("Illegal SendUnary " + low, context);
                        }
                        break;

                    case 11: {// SendBinary
                        if ((stack[stackTop - 1] instanceof SmallInt) &&
                                (stack[stackTop - 2] instanceof SmallInt)) {
                            int j = ((SmallInt) stack[--stackTop]).value;
                            int i = ((SmallInt) stack[--stackTop]).value;
                            boolean done = true;
                            switch (low) {
                                case 0: // <
                                    returnedValue = (i < j) ? trueObject : falseObject;
                                    break;
                                case 1: // <=
                                    returnedValue = (i <= j) ? trueObject : falseObject;
                                    break;
                                case 2: // +
                                    long li = i + (long) j;
                                    if (li != (i + j))
                                        done = false; // overflow
                                    returnedValue = newInteger(i + j);
                                    break;
                            }
                            if (done) {
                                stack[stackTop++] = returnedValue;
                                break;
                            } else {
                                stackTop += 2; // overflow, send message
                            }
                        }
                        // non optimized binary message
                        arguments = new SmallObject(ArrayClass, 2);
                        arguments.data[1] = stack[--stackTop];
                        arguments.data[0] = stack[--stackTop];
                        contextData[5] = newInteger(stackTop);
                        contextData[4] = newInteger(bytePointer);
                        SmallByteArray msg = null;
                        switch (low) {
                            case 0:
                                msg = new SmallByteArray(null, "<");
                                break;
                            case 1:
                                msg = new SmallByteArray(null, "<=");
                                break;
                            case 2:
                                msg = new SmallByteArray(null, "+");
                                break;
                        }
                        method = methodLookup(arguments.data[0].objClass, msg, context, arguments);
                        context = buildContext(context, arguments, method);
                        contextData = context.data;
                        continue outerLoop;
                    }

                    case 13: // Do Primitive, low is arg count, next byte is number
                        high = (int) code[bytePointer++] & 0x0FF;
                        //System.out.println("do primitive: " + high);
                        switch (high) {

                            case 1: // object identity
                                returnedValue = stack[--stackTop];
                                if (returnedValue == stack[--stackTop]) {
                                    returnedValue = trueObject;
                                } else {
                                    returnedValue = falseObject;
                                }
                                break;

                            case 2: // object class
                                returnedValue = stack[--stackTop].objClass;
                                break;

                            case 4: // object size
                                returnedValue = stack[--stackTop];
                                if (returnedValue instanceof SmallByteArray) {
                                    low = ((SmallByteArray) returnedValue).values.length;
                                } else {
                                    low = returnedValue.data.length;
                                }
                                returnedValue = newInteger(low);
                                break;

                            case 5: // object at put
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = stack[--stackTop];
                                returnedValue.data[low - 1] = stack[--stackTop];
                                break;

                            case 6: // new context execute
                                returnedValue = execute(stack[--stackTop], myThread, parentThread);
                                break;

                            case 7: // new object allocation
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = new SmallObject(stack[--stackTop], low);
                                while (low > 0) {
                                    returnedValue.data[--low] = nilObject;
                                }
                                break;

                            case 8: { // block invocation
                                returnedValue = stack[--stackTop]; // the block
                                high = ((SmallInt) returnedValue.data[7]).value; // arg location
                                low -= 2;
                                if (low >= 0) {
                                    temporaries = returnedValue.data[2].data;
                                    while (low >= 0) {
                                        temporaries[high + low--] = stack[--stackTop];
                                    }
                                }
                                contextData[5] = newInteger(stackTop);
                                contextData[4] = newInteger(bytePointer);

                                SmallObject newContext = new SmallObject(ContextClass, 10);
                                System.arraycopy(returnedValue.data, 0, newContext.data, 0, 10);

                                newContext.data[6] = contextData[6];
                                newContext.data[5] = smallIntCache[0]; // stack top
                                newContext.data[4] = returnedValue.data[9]; // starting addr
                                low = newContext.data[3].data.length; //stack size
                                newContext.data[3] = new SmallObject(ArrayClass, low); // new stack
                                context = newContext;
                                contextData = context.data;
                                continue outerLoop;
                            }

                            case 9: // read a char from input
                                try {
                                    returnedValue = newInteger(System.in.read());
                                } catch (IOException e) {
                                    returnedValue = nilObject;
                                }
                                break;

                            case 10: { // small integer addition need to handle ovflow
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                long lhigh = ((long) high) + (long) low;
                                high += low;
                                if (lhigh == high) returnedValue = newInteger(high);
                                else returnedValue = nilObject;
                            }
                            break;

                            case 11: // small integer quotient
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                high /= low;
                                returnedValue = newInteger(high);
                                break;

                            case 12: // small integer remainder
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                high %= low;
                                returnedValue = newInteger(high);
                                break;

                            case 14: // small int equality
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = (low == high) ? trueObject : falseObject;
                                break;

                            case 15: { // small integer multiplication
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                long lhigh = ((long) high) * (long) low;
                                high *= low;
                                if (lhigh == high) returnedValue = newInteger(high);
                                else returnedValue = nilObject;
                            }
                            break;

                            case 16: { // small integer subtraction
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                long lhigh = ((long) high) - (long) low;
                                high -= low;
                                if (lhigh == high) returnedValue = newInteger(high);
                                else returnedValue = nilObject;
                            }
                            break;

                            case 17: // small integer as string
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = new SmallByteArray(stack[--stackTop], String.valueOf(low));
                                break;

                            case 18: // debug -- dummy for now
                                returnedValue = stack[--stackTop];
                                System.out.println("Debug " + returnedValue + " class " + returnedValue.objClass.data[0]);
                                break;

                            case 19: {// block fork
                                returnedValue = stack[--stackTop];
                                taskManager.postTask(new ActionTask(SmallInterpreter.this, returnedValue));
                            }
                            break;

                            case 20: // byte array allocation
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = new SmallByteArray(stack[--stackTop], low);
                                break;

                            case 21: // string at
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = stack[--stackTop];
                                SmallByteArray baa = (SmallByteArray) returnedValue;
                                low = (int) baa.values[low - 1] & 0x0FF;
                                returnedValue = newInteger(low);
                                break;

                            case 22: // string at put
                                low = ((SmallInt) stack[--stackTop]).value;
                                SmallByteArray ba = (SmallByteArray) stack[--stackTop];
                                high = ((SmallInt) stack[--stackTop]).value;
                                ba.values[low - 1] = (byte) high;
                                returnedValue = ba;
                                break;

                            case 23: // string copy
                                returnedValue = stack[--stackTop];
                                returnedValue = stack[--stackTop].copy(returnedValue);
                                break;

                            case 24: { // string append
                                SmallByteArray a = (SmallByteArray) stack[--stackTop];
                                SmallByteArray b = (SmallByteArray) stack[--stackTop];
                                low = a.values.length + b.values.length;
                                SmallByteArray n = new SmallByteArray(a.objClass, low);
                                high = 0;
                                for (int i = 0; i < a.values.length; i++) {
                                    n.values[high++] = a.values[i];
                                }
                                for (int i = 0; i < b.values.length; i++) {
                                    n.values[high++] = b.values[i];
                                }
                                returnedValue = n;
                            }
                            break;

                            case 26: { // string compare
                                SmallByteArray a = (SmallByteArray) stack[--stackTop];
                                SmallByteArray b = (SmallByteArray) stack[--stackTop];
                                low = a.values.length;
                                high = b.values.length;
                                int s = Math.min(low, high);
                                int r = 0;
                                for (int i = 0; i < s; i++)
                                    if (a.values[i] < b.values[i]) {
                                        r = 1;
                                        break;
                                    } else if (b.values[i] < a.values[i]) {
                                        r = -1;
                                        break;
                                    }
                                if (r == 0) {
                                    if (low < high) {
                                        r = 1;
                                    } else if (low > high) {
                                        r = -1;
                                    }
                                }
                                returnedValue = newInteger(r);
                            }
                            break;

                            case 29: { // image export (was image save)
                                SmallByteArray a = (SmallByteArray) stack[--stackTop];
                                String name = a.toString();
                                try {
                                    saveImageToOutputStream(new FileOutputStream(name));
                                } catch (Exception e) {
                                    throw new SmallException("got I/O Exception " + e, context);
                                }
                                returnedValue = a;
                            }
                            break;

                            case 30: {// array at
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = stack[--stackTop];
                                returnedValue = returnedValue.data[low - 1];
                            }
                            break;

                            case 31: {// array with:  (add new item)
                                SmallObject oldar = stack[--stackTop];
                                low = oldar.data.length;
                                returnedValue = new SmallObject(oldar.objClass, low + 1);
                                for (int i = 0; i < low; i++) {
                                    returnedValue.data[i] = oldar.data[i];
                                }
                                returnedValue.data[low] = stack[--stackTop];
                            }
                            break;

                            case 32: { // object add: increase object size
                                returnedValue = stack[--stackTop];
                                low = returnedValue.data.length;
                                SmallObject[] na = new SmallObject[low + 1];
                                for (int i = 0; i < low; i++) {
                                    na[i] = returnedValue.data[i];
                                }
                                na[low] = stack[--stackTop];
                                returnedValue.data = na;
                            }
                            break;

                            case 33: {// Sleep for a bit
                                low = ((SmallInt) stack[--stackTop]).value;
                                try {
                                    Thread.sleep(low);
                                } catch (Exception a) {
                                }
                            }
                            break;

                            case 34: { // thread kill
                                if (parentThread != null) {
                                    parentThread.stop();
                                }
                                if (myThread != null) {
                                    myThread.stop();
                                }
                                System.out.println("is there life after death?");
                            }
                            break;

                            case 35: // return current context
                                returnedValue = context;
                                break;

                            case 36:  // fast array creation
                                returnedValue = new SmallObject(ArrayClass, low);
                                for (int i = low - 1; i >= 0; i--) {
                                    returnedValue.data[i] = stack[--stackTop];
                                }
                                break;

                            case 41: {// open file for output
                                try {
                                    FileOutputStream of = new FileOutputStream(stack[--stackTop].toString());
                                    PrintStream ps = new PrintStream(of);
                                    returnedValue = new SmallJavaObject(stack[--stackTop], ps);
                                } catch (IOException e) {
                                    throw new SmallException("I/O exception " + e, context);
                                }
                            }
                            break;

                            case 42: {// open file for input
                                try {
                                    FileInputStream of = new FileInputStream(stack[--stackTop].toString());
                                    DataInput ps = new DataInputStream(of);
                                    returnedValue = new SmallJavaObject(stack[--stackTop], ps);
                                } catch (IOException e) {
                                    throw new SmallException("I/O exception " + e, context);
                                }
                            }
                            break;

                            case 43: {// write a string
                                try {
                                    PrintStream ps = (PrintStream)
                                            ((SmallJavaObject) stack[--stackTop]).value;
                                    ps.print(stack[--stackTop]);
                                } catch (Exception e) {
                                    throw new SmallException("I/O exception " + e, context);
                                }
                            }
                            break;

                            case 44: { // read a string
                                try {
                                    DataInput di = (DataInput)
                                            ((SmallJavaObject) stack[--stackTop]).value;
                                    String line = di.readLine();
                                    if (line == null) {
                                        --stackTop;
                                        returnedValue = nilObject;
                                    } else {
                                        returnedValue = new SmallByteArray(stack[--stackTop], line);
                                    }
                                } catch (EOFException e) {
                                    returnedValue = nilObject;
                                } catch (IOException f) {
                                    throw new SmallException("I/O exception " + f, context);
                                }
                            }
                            break;

                            case 50:  // integer into float
                                low = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = new SmallJavaObject(stack[--stackTop], (double) low);
                                break;

                            case 51: { // addition of float
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                double b = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = new SmallJavaObject(stack[--stackTop], a + b);
                            }
                            break;

                            case 52: { // subtraction of float
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                double b = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = new SmallJavaObject(stack[--stackTop], a - b);
                            }
                            break;

                            case 53: { // multiplication of float
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                double b = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = new SmallJavaObject(stack[--stackTop], a * b);
                            }
                            break;

                            case 54: { // division of float
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                double b = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = new SmallJavaObject(stack[--stackTop], a / b);
                            }
                            break;

                            case 55: { // less than test of float
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                double b = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = (a < b) ? trueObject : falseObject;
                            }
                            break;

                            case 56: { // equality test of float
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                double b = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = (a == b) ? trueObject : falseObject;
                            }
                            break;

                            case 57: { // float to int
                                double a = (Double) ((SmallJavaObject) stack[--stackTop]).value;
                                returnedValue = newInteger((int) a);
                            }
                            break;

                            case 58: // random float
                                returnedValue = new SmallJavaObject(stack[--stackTop], Math.random());
                                break;

                            case 59: // print of float
                                returnedValue = stack[--stackTop];
                                returnedValue = new SmallByteArray(stack[--stackTop],
                                        String.valueOf(((Double) ((SmallJavaObject) returnedValue).value).doubleValue()));
                                break;

                            case 60: { // make window
                                returnedValue = new SmallJavaObject(stack[--stackTop], screen.createWindow());
                            }
                            break;

                            case 61: { // show/hide text window
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jo = (SmallJavaObject) stack[--stackTop];
                                if (returnedValue == trueObject) {
                                    screen.showWindow(jo.value);
                                } else {
                                    screen.hideWindow(jo.value);
                                }
                            }
                            break;

                            case 62: { // set content pane
                                SmallJavaObject contentHolder = (SmallJavaObject) stack[--stackTop];
                                returnedValue = stack[--stackTop];
                                SmallJavaObject dialogHolder = (SmallJavaObject) returnedValue;

                                screen.setWindowContent(dialogHolder.value, contentHolder.value);
                            }
                            break;

                            case 63: {  // set size
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;
                                returnedValue = stack[--stackTop];

                                SmallJavaObject wo = (SmallJavaObject) returnedValue;
                                screen.setWindowSize(wo.value, low, high);
                            }
                            break;

                            case 64: { // add menu to window
                                SmallJavaObject menu = (SmallJavaObject) stack[--stackTop];
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jo = (SmallJavaObject) returnedValue;

                                screen.addMenu(jo.value, menu.value);
                            }
                            break;

                            case 65: { // set title
                                SmallObject title = stack[--stackTop];
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jd = (SmallJavaObject) returnedValue;

                                screen.setWindowTitle(jd.value, title.toString());
                            }
                            break;

                            case 66: { // repaint window
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jd = (SmallJavaObject) returnedValue;

                                screen.repaintWindow(jd.value);
                            }
                            break;

                            case 70: { // new label panel
                                String content = stack[--stackTop].toString();
                                returnedValue = new SmallJavaObject(stack[--stackTop], screen.createPanel(content));
                            }
                            break;

                            case 71: { // new button
                                final SmallObject action = stack[--stackTop];
                                /*final JButton jb = new JButton(stack[--stackTop].toString());
                                returnedValue = new SmallJavaObject(stack[--stackTop], jb);
                                jb.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action));
                                    }
                                });*/

                                String text = stack[--stackTop].toString();
                                Object button = screen.createButton(text, new ButtonListener() {
                                    @Override
                                    public void onClick() {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action));
                                    }
                                });
                                returnedValue = new SmallJavaObject(stack[--stackTop], button);
                            }
                            break;

                            case 72: { // new text line
                                returnedValue = new SmallJavaObject(stack[--stackTop], screen.createTextLine());
                                break;
                            }

                            case 73: { // new text area
                                returnedValue = new SmallJavaObject(stack[--stackTop], screen.createTextArea());
                                break;
                            }

                            case 74: { // new grid panel
                                SmallObject dataHolder = stack[--stackTop];
                                low = ((SmallInt) stack[--stackTop]).value;
                                high = ((SmallInt) stack[--stackTop]).value;

                                Object panel = screen.createGridPanel(dataHolder.data, low, high);
                                returnedValue = new SmallJavaObject(stack[--stackTop], panel);

                                /*JPanel jp = new JPanel();
                                jp.setLayout(new GridLayout(low, high));
                                for (int i = 0; i < data.data.length; i++) {
                                    jp.add((Component) ((SmallJavaObject) data.data[i]).value);
                                }*/
                            }
                            break;

                            case 75: { // new list panel
                                final SmallObject action = stack[--stackTop];
                                SmallObject dataHolder = stack[--stackTop];
                                returnedValue = stack[--stackTop];
                                Object listPanel = screen.createListPanel(dataHolder.data, new ListListener() {
                                    @Override
                                    public void onItemClick(int zeroBaseIndex) {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action, zeroBaseIndex + 1));
                                    }
                                });

                                returnedValue = new SmallJavaObject(returnedValue, listPanel);
                                /*final JList<SmallObject> jl = new JList<>(data.data);
                                jl.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                                jl.addListSelectionListener(
                                        new ListSelectionListener() {
                                            public void valueChanged(ListSelectionEvent e) {
                                                if ((!e.getValueIsAdjusting()) && (jl.getSelectedIndex() >= 0)) {
                                                    taskManager.postTask(new ActionTask(SmallInterpreter.this, action, jl.getSelectedIndex() + 1));
                                                }
                                            }
                                        });*/
                            }
                            break;

                            case 76: { // new border panel
                                Object borderPanel = screen.createBorderPanel();
                                //JPanel jp = new JPanel();
                                //jp.setLayout(new BorderLayout());
                                returnedValue = stack[--stackTop];
                                if (returnedValue != nilObject) {
                                    screen.addToBorder(borderPanel, IScreen.POSITION_CENTER, ((SmallJavaObject) returnedValue).value);
                                    //jp.add("Center", (Component) ((SmallJavaObject) returnedValue).value);
                                }
                                returnedValue = stack[--stackTop];
                                if (returnedValue != nilObject) {
                                    screen.addToBorder(borderPanel, IScreen.POSITION_LEFT, ((SmallJavaObject) returnedValue).value);
                                    //jp.add("West", (Component) ((SmallJavaObject) returnedValue).value);
                                }
                                returnedValue = stack[--stackTop];
                                if (returnedValue != nilObject) {
                                    screen.addToBorder(borderPanel, IScreen.POSITION_RIGHT, ((SmallJavaObject) returnedValue).value);
                                    //jp.add("East", (Component) ((SmallJavaObject) returnedValue).value);
                                }
                                returnedValue = stack[--stackTop];
                                if (returnedValue != nilObject) {
                                    screen.addToBorder(borderPanel, IScreen.POSITION_BOTTOM, ((SmallJavaObject) returnedValue).value);
                                    //jp.add("South", (Component) ((SmallJavaObject) returnedValue).value);
                                }
                                returnedValue = stack[--stackTop];
                                if (returnedValue != nilObject) {
                                    screen.addToBorder(borderPanel, IScreen.POSITION_TOP, ((SmallJavaObject) returnedValue).value);
                                    //jp.add("North", (Component) ((SmallJavaObject) returnedValue).value);
                                }
                                returnedValue = new SmallJavaObject(stack[--stackTop], borderPanel);
                            }
                            break;

                            case 77: { // set image on label
                                SmallJavaObject imgHolder = (SmallJavaObject) stack[--stackTop];
                                SmallJavaObject panelHolder = (SmallJavaObject) stack[--stackTop];
                                Object target = panelHolder.value;
                                Object img = imgHolder.value;
                                screen.addImageToLabel(target, img);
                                /*if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                if (jo instanceof JLabel) {
                                    JLabel jlb = (JLabel) jo;
                                    jlb.setIcon(new ImageIcon((Image) img.value));
                                    jlb.setHorizontalAlignment(SwingConstants.LEFT);
                                    jlb.setVerticalAlignment(SwingConstants.TOP);
                                    jlb.repaint();
                                }*/
                            }
                            break;

                            case 79: {// repaint
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jo = (SmallJavaObject) returnedValue;
                                //((JComponent) jo.value).repaint();
                                screen.repaintComponent(jo.value);
                            }
                            break;

                            case 80: { // content of text area
                                SmallJavaObject jt = (SmallJavaObject) stack[--stackTop];
                                returnedValue = stack[--stackTop]; // class
                                Object target = jt.value;
                                String text = screen.getText(target);
                                returnedValue = new SmallByteArray(returnedValue, text);
                                /*if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                if (jo instanceof JTextComponent) {
                                    returnedValue = new SmallByteArray(returnedValue, ((JTextComponent) jo).getText());
                                } else {
                                    returnedValue = new SmallByteArray(returnedValue, "");
                                }*/
                            }
                            break;

                            case 81: {// content of selected text area
                                SmallJavaObject jt = (SmallJavaObject) stack[--stackTop];
                                returnedValue = stack[--stackTop]; // class
                                Object target = jt.value;
                                String text = screen.getSelectedText(target);
                                returnedValue = new SmallByteArray(returnedValue, text);
                                /*if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                if (jo instanceof JTextComponent) {
                                    returnedValue = new SmallByteArray(returnedValue, ((JTextComponent) jo).getSelectedText());
                                } else {
                                    returnedValue = new SmallByteArray(returnedValue, "");
                                }*/
                            }
                            break;

                            case 82: { // set text area
                                returnedValue = stack[--stackTop];// text
                                SmallJavaObject jt = (SmallJavaObject) stack[--stackTop];
                                Object target = jt.value;
                                screen.setText(target, returnedValue.toString());
                                /*if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                if (jo instanceof JTextComponent) {
                                    ((JTextComponent) jo).setText(returnedValue.toString());
                                }*/
                            }
                            break;

                            case 83: { // get selected index
                                SmallJavaObject jo = (SmallJavaObject) stack[--stackTop];
                                Object jl = jo.value;
                                if (jl instanceof JScrollPane) {
                                    jl = ((JScrollPane) jl).getViewport().getView();
                                }
                                if (jl instanceof JList) {
                                    returnedValue = newInteger(((JList) jl).getSelectedIndex() + 1);
                                } else if (jl instanceof JScrollBar) {
                                    returnedValue = newInteger(((JScrollBar) jl).getValue());
                                } else {
                                    returnedValue = newInteger(0);
                                }
                            }
                            break;

                            case 84: { // set list data
                                SmallObject data = stack[--stackTop];
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jo = (SmallJavaObject) returnedValue;
                                Object jl = jo.value;
                                if (jl instanceof JScrollPane) {
                                    jl = ((JScrollPane) jl).getViewport().getView();
                                }
                                if (jl instanceof JList) {
                                    ((JList) jl).setListData(data.data);
                                    ((JList) jl).repaint();
                                }
                            }
                            break;

                            case 85: { // new slider
                                final SmallObject action = stack[--stackTop];
                                int max = ((SmallInt) stack[--stackTop]).value + 10; //why?
                                int min = ((SmallInt) stack[--stackTop]).value;
                                SmallObject orient = stack[--stackTop];
                                final JScrollBar bar = new JScrollBar(
                                        ((orient == trueObject) ? JScrollBar.VERTICAL : JScrollBar.HORIZONTAL),
                                        min, 10, min, max);
                                returnedValue = new SmallJavaObject(stack[--stackTop], bar);
                                if (action != nilObject) {
                                    bar.addAdjustmentListener(new AdjustmentListener() {
                                        public void adjustmentValueChanged(AdjustmentEvent ae) {
                                            taskManager.postTask(new ActionTask(SmallInterpreter.this, action, ae.getValue()));
                                        }
                                    });
                                }
                            }
                            break;

                            case 86: { // onMouseDown b
                                final SmallObject action = stack[--stackTop];
                                SmallJavaObject pan = (SmallJavaObject) stack[--stackTop];
                                Object jo = pan.value;
                                if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                final JComponent jpan = (JComponent) jo;
                                jpan.addMouseListener(new MouseAdapter() {
                                    public void mousePressed(MouseEvent e) {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action, e.getX(), e.getY()));
                                    }
                                });
                            }
                            break;

                            case 87: { // onMouseUp b
                                final SmallObject action = stack[--stackTop];
                                SmallJavaObject pan = (SmallJavaObject) stack[--stackTop];
                                Object jo = pan.value;
                                if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                final JComponent jpan = (JComponent) jo;
                                jpan.addMouseListener(new MouseAdapter() {
                                    public void mouseReleased(MouseEvent e) {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action, e.getX(), e.getY()));
                                    }
                                });
                            }
                            break;

                            case 88: { // onMouseMove b
                                final SmallObject action = stack[--stackTop];
                                SmallJavaObject pan = (SmallJavaObject) stack[--stackTop];
                                Object jo = pan.value;
                                if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                final JComponent jpan = (JComponent) jo;
                                jpan.addMouseMotionListener(new MouseMotionAdapter() {
                                    public void mouseDragged(MouseEvent e) {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action, e.getX(), e.getY()));
                                    }

                                    public void mouseMoved(MouseEvent e) {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action, e.getX(), e.getY()));
                                    }
                                });
                            }
                            break;

                            case 89: { // set selected text area
                                returnedValue = stack[--stackTop];// text
                                SmallJavaObject jt = (SmallJavaObject) stack[--stackTop];
                                Object target = jt.value;
                                screen.replaceSelectedText(target, returnedValue.toString());
                                /*if (jo instanceof JScrollPane) {
                                    jo = ((JScrollPane) jo).getViewport().getView();
                                }
                                if (jo instanceof JTextComponent) {
                                    ((JTextComponent) jo).replaceSelection(returnedValue.toString());
                                }*/
                            }
                            break;

                            case 90: { // new menu
                                SmallObject title = stack[--stackTop]; // text
                                returnedValue = stack[--stackTop]; // class
                                //JMenu menu = new JMenu(title.toString());
                                Object menu = screen.createMenu(title.toString());
                                returnedValue = new SmallJavaObject(returnedValue, menu);
                            }
                            break;

                            case 91: { // new menu item
                                final SmallObject action = stack[--stackTop];
                                final SmallObject text = stack[--stackTop];
                                returnedValue = stack[--stackTop];
                                SmallJavaObject mo = (SmallJavaObject) returnedValue;
                                screen.addMenuItem(mo.value, text.toString(), new ButtonListener() {
                                    @Override
                                    public void onClick() {
                                        taskManager.postTask(new ActionTask(SmallInterpreter.this, action));
                                    }
                                });
                                /*JMenu menu = (JMenu) mo.value;
                                JMenuItem ji = new JMenuItem(text.toString());
                                ji.addActionListener(
                                        new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                taskManager.postTask(new ActionTask(SmallInterpreter.this, action));
                                            }
                                        });
                                menu.add(ji);*/
                            }
                            break;

                            case 100: // new semaphore
                                returnedValue = new SmallJavaObject(stack[--stackTop], new Sema());
                                break;

                            case 101: { // semaphore wait
                                SmallJavaObject jo = (SmallJavaObject) stack[--stackTop];
                                returnedValue = ((Sema) jo.value).get();
                            }
                            break;

                            case 102: { // semaphore set
                                returnedValue = stack[--stackTop];
                                SmallJavaObject jo = (SmallJavaObject) stack[--stackTop];
                                ((Sema) jo.value).set(returnedValue);
                            }
                            break;

                            case 116: { // image save
                                try {
                                    // We have a problem - java won't allow us to amend a jar/zip file
                                    // even though the jar tool does it without problems.
                                    //
                                    // So we must read all the files in the jar file (except 'image' of course)
                                    // one by one into a new jar file, and then swap it for the old one.
                                    //
                                    // This works because the vm+image is still quite small.
                                    //
                                    // This is a lot of effort, but libraries which do this for us are
                                    // relatively large (TrueZip, for example, is over 350K) and would require
                                    // us to take on board even more complexity if we were to deploy in a single
                                    // jar file (which is, after all, the point of the exercise!)

                                    // First off, get name of jar
                                    String nameOfJar = this.getClass().getClassLoader().getSystemResource("image").getFile();
                                    nameOfJar = nameOfJar.substring(5, nameOfJar.length() - 7);
                                    JarFile jar = new JarFile(nameOfJar);

                                    // Create temp file and access existing file

                                    File tempJar = null;
                                    JarOutputStream newJar = null;
                                    tempJar = File.createTempFile("tempJarFile", null);
                                    newJar = new JarOutputStream(new FileOutputStream(tempJar));
                                    // } catch (Exception e) {JOptionPane.showMessageDialog(new JFrame("X"), "1");}

                                    byte[] buffer = new byte[1024];
                                    int bytesRead;

                                    // Add back the original files, except image
                                    Enumeration entries = jar.entries();
                                    while (entries.hasMoreElements()) {
                                        JarEntry entry = (JarEntry) entries.nextElement();
                                        if (entry.getName().contentEquals("image") == false) {
                                            InputStream is = jar.getInputStream(entry);
                                            newJar.putNextEntry(entry);
                                            while ((bytesRead = is.read(buffer)) != -1) {
                                                newJar.write(buffer, 0, bytesRead);
                                            }
                                        }
                                    }

                                    // Add new file last
                                    JarEntry entry = new JarEntry("image");
                                    newJar.putNextEntry(entry);
                                    saveImageToOutputStream(newJar);
                                    newJar.close();
                                    jar.close();


                                    File origFile = new File(nameOfJar);
                                    origFile.delete();
                                    tempJar.renameTo(origFile);

                                    returnedValue = trueObject;
                                } catch (Exception e) {
                                    throw new SmallException("I/O exception " + e, context);
                                }
                            }
                            break;

                            case 117: {  // Quit without saving!
                                System.exit(0);
                            }
                            break;

                            case 118: { // onWindow close b
                                try {
                                    final SmallObject action = stack[--stackTop];
                                    SmallJavaObject pan = (SmallJavaObject) stack[--stackTop];
                                    JDialog jo = (JDialog) pan.value;
                                    jo.addWindowListener(new WindowAdapter() {
                                        public void windowClosing(WindowEvent e) {
                                            taskManager.postTask(new ActionTask(SmallInterpreter.this, action));
                                        }
                                    });
                                } catch (Exception e) {
                                    throw new SmallException("Exception: " + e.toString(), context);
                                }
                            }
                            break;

                            case 119: {  // Millisecond clock
                                returnedValue = newInteger((int) java.lang.System.currentTimeMillis());
                            }
                            break;


                            default:
                                throw new SmallException("Unknown Primitive " + high, context);
                        }
                        stack[stackTop++] = returnedValue;
                        break;

                    case 15: // Do Special
                        switch (low) {
                            case 1: // self return
                                if (arguments == null) {
                                    arguments = contextData[1];
                                }
                                returnedValue = arguments.data[0];
                                context = contextData[6]; // previous context
                                break innerLoop;

                            case 2: // stack return
                                returnedValue = stack[--stackTop];
                                context = contextData[6]; // previous context
                                break innerLoop;

                            case 3: // block return
                                returnedValue = stack[--stackTop];
                                context = contextData[8]; // creating context in block
                                context = context.data[6]; // previous context
                                break innerLoop;

                            case 4: // duplicate
                                returnedValue = stack[stackTop - 1];
                                stack[stackTop++] = returnedValue;
                                break;

                            case 5: // pop top
                                stackTop--;
                                break;

                            case 6: // branch
                                low = (int) code[bytePointer++] & 0x0FF;
                                bytePointer = low;
                                break;

                            case 7: // branch if true
                                low = (int) code[bytePointer++] & 0x0FF;
                                returnedValue = stack[--stackTop];
                                if (returnedValue == trueObject) {
                                    bytePointer = low;
                                }
                                break;

                            case 8: // branch if false
                                low = (int) code[bytePointer++] & 0x0FF;
                                returnedValue = stack[--stackTop];
                                if (returnedValue == falseObject) {
                                    bytePointer = low;
                                }
                                break;

                            case 11: // send to super
                                low = (int) code[bytePointer++] & 0x0FF;
                                // message selector
                                // save old context
                                arguments = stack[--stackTop];
                                contextData[5] = newInteger(stackTop);
                                contextData[4] = newInteger(bytePointer);
                                // now build new context
                                if (literals == null) {
                                    literals = method.data[2].data;
                                }
                                if (method == null) {
                                    method = context.data[0];
                                }
                                method = method.data[5]; // class in method
                                method = method.data[1]; // parent in class
                                method = methodLookup(method, (SmallByteArray) literals[low], context, arguments);
                                context = buildContext(context, arguments, method);
                                contextData = context.data;
                                // load information from context
                                continue outerLoop;

                            default: // throw exception
                                throw new SmallException("Unrecogized DoSpecial " + low, context);
                        }
                        break;

                    default:   // throw exception
                        throw new SmallException("Unrecogized opCode " + low, context);
                }
            } // end of inner loop

            if ((context == null) || (context == nilObject)) {
                //System.out.println("lookups " + lookup + " cached " + cached);
                return returnedValue;
            }
            contextData = context.data;
            stack = contextData[3].data;
            stackTop = ((SmallInt) contextData[5]).value;
            stack[stackTop++] = returnedValue;
            contextData[5] = newInteger(stackTop);
        }

    } // end of outer loop


    public static class ActionThread extends Thread {

        private SmallObject action;
        private Thread myThread;
        private SmallInterpreter interpreter;


        public ActionThread(SmallInterpreter vm, SmallObject block, Thread myT) {
            myThread = myT;
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, 10);
            System.arraycopy(block.data, 0, action.data, 0, 10);
        }

        public ActionThread(SmallInterpreter vm, SmallObject block, Thread myT, int v1) {
            myThread = myT;
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, 10);
            System.arraycopy(block.data, 0, action.data, 0, 10);

            int argLoc = ((SmallInt) action.data[7]).value;
            action.data[2].data[argLoc] = vm.newInteger(v1);
        }

        public ActionThread(SmallInterpreter vm, SmallObject block, Thread myT, int v1, int v2) {
            myThread = myT;
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, 10);
            System.arraycopy(block.data, 0, action.data, 0, 10);

            int argLoc = ((SmallInt) action.data[7]).value;
            action.data[2].data[argLoc] = vm.newInteger(v1);
            action.data[2].data[argLoc + 1] = vm.newInteger(v2);
        }


        public void run() {
            int stksize = action.data[3].data.length;
            action.data[3] = new SmallObject(interpreter.ArrayClass, stksize); // new stack
            action.data[4] = action.data[9]; // byte pointer
            action.data[5] = interpreter.newInteger(0); // stack top
            action.data[6] = interpreter.nilObject;
            try {
                interpreter.execute(action, this, myThread);
            } catch (Exception e) {
                System.out.println("caught exception " + e);
            }
        }
    }

    public static class ActionContext implements Runnable {
        private SmallObject action;
        private SmallInterpreter interpreter;

        public ActionContext(SmallInterpreter vm, SmallObject context) {
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, context.data.length);
            System.arraycopy(context.data, 0, action.data, 0, context.data.length);
        }

        @Override
        public void run() {
            try {
                interpreter.execute(action, null, null);
            } catch (Exception e) {
                System.out.println("caught exception.");
                e.printStackTrace();
            }
        }
    }

    public static class ActionBlock implements Runnable {
        private SmallObject action;
        private SmallInterpreter interpreter;

        public ActionBlock(SmallInterpreter vm, SmallObject block) {
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, 10);
            System.arraycopy(block.data, 0, action.data, 0, 10);
        }

        public ActionBlock(SmallInterpreter vm, SmallObject block, int v1) {
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, 10);
            System.arraycopy(block.data, 0, action.data, 0, 10);

            int argLoc = ((SmallInt) action.data[7]).value;
            action.data[2].data[argLoc] = vm.newInteger(v1);
        }

        public ActionBlock(SmallInterpreter vm, SmallObject block, int v1, int v2) {
            interpreter = vm;
            action = new SmallObject(vm.ContextClass, 10);
            System.arraycopy(block.data, 0, action.data, 0, 10);

            int argLoc = ((SmallInt) action.data[7]).value;
            action.data[2].data[argLoc] = vm.newInteger(v1);
            action.data[2].data[argLoc + 1] = vm.newInteger(v2);
        }


        public void run() {
            int stackSize = action.data[3].data.length;
            action.data[3] = new SmallObject(interpreter.ArrayClass, stackSize); // new stack
            action.data[4] = action.data[9]; // byte pointer
            action.data[5] = interpreter.newInteger(0); // stack top
            action.data[6] = interpreter.nilObject;
            try {
                interpreter.execute(action, null, null);
            } catch (Exception e) {
                System.out.println("caught exception " + e);
            }
        }
    }

    public static class ActionTask implements Runnable {
        private SmallObject action;
        private SmallInterpreter interpreter;

        public ActionTask(SmallInterpreter vm, SmallObject block) {
            interpreter = vm;
            int dataSize = block.data.length;
            action = new SmallObject(vm.ContextClass, dataSize);
            System.arraycopy(block.data, 0, action.data, 0, dataSize);
        }

        public ActionTask(SmallInterpreter vm, SmallObject block, int v1) {
            interpreter = vm;
            int dataSize = block.data.length;
            action = new SmallObject(vm.ContextClass, dataSize);
            System.arraycopy(block.data, 0, action.data, 0, dataSize);

            if (dataSize >= 8) {
                int argLoc = ((SmallInt) action.data[7]).value;
                action.data[2].data[argLoc] = vm.newInteger(v1);
            }
        }

        public ActionTask(SmallInterpreter vm, SmallObject block, int v1, int v2) {
            interpreter = vm;
            int dataSize = block.data.length;
            action = new SmallObject(vm.ContextClass, dataSize);
            System.arraycopy(block.data, 0, action.data, 0, dataSize);

            if (dataSize >= 8) {
                int argLoc = ((SmallInt) action.data[7]).value;
                action.data[2].data[argLoc] = vm.newInteger(v1);
                action.data[2].data[argLoc + 1] = vm.newInteger(v2);
            }
        }


        public void run() {
            int stackSize = action.data[3].data.length;
            action.data[3] = new SmallObject(interpreter.ArrayClass, stackSize); // new stack
            action.data[4] = action.data[9]; // byte pointer
            action.data[5] = interpreter.newInteger(0); // stack top
            action.data[6] = interpreter.nilObject;
            try {
                interpreter.execute(action, null, null);
            } catch (Exception e) {
                System.out.println("caught exception " + e);
            }
        }
    }

}

