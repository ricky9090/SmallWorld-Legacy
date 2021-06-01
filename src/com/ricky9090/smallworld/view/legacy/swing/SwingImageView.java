package com.ricky9090.smallworld.view.legacy.swing;

import com.ricky9090.smallworld.view.legacy.STImageView;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

public class SwingImageView extends SwingBaseView implements STImageView {

    private final BufferedImage mImage;

    public SwingImageView() {
        mImage = new BufferedImage(1, 1, ColorSpace.TYPE_RGB);
    }

    @Override
    public Object getRealComponent() {
        return null;
    }

    @Override
    public void repaint() {

    }
}
