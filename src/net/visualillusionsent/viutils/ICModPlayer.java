package net.visualillusionsent.viutils;

public interface ICModPlayer {
    
    public String getName();
    public void sendMessage(String message);
    public void notify(String message);
    public void dropInventory();
    public boolean canUseCommand(String cmd);
    public void moveTo(double x, double y, double z, float rotation, float pitch);
    public int getHealth();
    public void setHealth(int newVal);
    public void doDamage(int val);
    public void heal(int val);
    public boolean getMode();
    public boolean isDamageDisabled();
    public double getX();
    public double getY();
    public double getZ();
    public double getRotation();
    public double getPitch();
    public String getWorldName();
    public int getDimension();
    public int getDimIndex();
    public boolean isInGroup(String group);
    public boolean isAdmin();
    
    public String toString();
    public boolean equals(Object obj);
    public int hashCode();
}
