package ru.sawimmod.roster;

public interface TreeNode {
    public static final byte PROTOCOL = 0;
    public static final byte GROUP = 1;
    public static final byte CONTACT = 2;

    public abstract byte getType();

    public abstract String getText();

    public abstract int getNodeWeight();
}