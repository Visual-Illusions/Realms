package net.visualillusionsent.viutils;

public interface ICModItem {

    public int getId();

    public int getDamage();

    public int getAmount();

    public int getSlot();

    public Object getItem();

    @Override
    public String toString();
}
