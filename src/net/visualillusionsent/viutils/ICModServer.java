package net.visualillusionsent.viutils;

import java.util.List;

public interface ICModServer {
    
    public List<ICModMob> getMobList();
    public List<ICModPlayer> getPlayerList();
    public List<ICModMob> getAnimalList();
    public ICModBlock getBlockAt(int x, int y, int z, int dim, String WorldName);
    public void setBlock(ICModBlock block);
    public void setBlock(int type, int x, int y, int z, String worldname, int dim);
    public String getDefaultWorldName();
    public int getHighestBlockY(int x, int y, String worldname, int dim);
    public String getDefaultGroup();
}
