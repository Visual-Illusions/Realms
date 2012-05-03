import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ICModServer;

/**
 * CanaryMod Server wrapper class
 * <p>
 * This class is a bridge to send Server data from the default <unnamed package> to
 * packaged classes.
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author darkdiplomat
 * @see ICModServer
 */
public class CModServer implements ICModServer{
    private Server server;
    
    public CModServer(Server server){
        this.server = server;
    }
    
    @Override
    public String getDefaultWorldName(){
        return server.getDefaultWorld().getName();   
    }

    @Override
    public List<ICModMob> getMobList() {
        List<ICModMob> mobList = new ArrayList<ICModMob>();
        try{
            for(String worldname : server.getMCServer().worlds.keySet()){
                OWorldServer[] dimensions = server.getMCServer().worlds.get(worldname);
                for(int i = 0; i < 3; i++){
                    World world = new World(dimensions[i]);
                    synchronized(world.getMobList()){
                        Iterator<Mob> iterator = world.getMobList().iterator();
                        while(iterator.hasNext()){
                            mobList.add(new CModMob(iterator.next()));
                        }
                    }
                }
            }
        }
        catch(ConcurrentModificationException cme){ }
        
        return mobList;
    }

    @Override
    public List<ICModPlayer> getPlayerList() {
        List<ICModPlayer> playerList = new ArrayList<ICModPlayer>();
        try{
            synchronized(server.getPlayerList()){
                Iterator<Player> iterator = server.getPlayerList().iterator();
                while(iterator.hasNext()){
                    playerList.add(new CModPlayer(iterator.next()));
                }
            }
        }
        catch(ConcurrentModificationException cme){ }
        
        return playerList;
    }

    @Override
    public List<ICModMob> getAnimalList() {
        List<ICModMob> animalList = new ArrayList<ICModMob>();
        try{
            for(String worldname : server.getMCServer().worlds.keySet()){
                OWorldServer[] dimensions = server.getMCServer().worlds.get(worldname);
                for(int i = 0; i < 3; i++){
                    World world = new World(dimensions[i]);
                    synchronized(world.getMobList()){
                        Iterator<Mob> iterator = world.getMobList().iterator();
                        while(iterator.hasNext()){
                            animalList.add(new CModMob(iterator.next()));
                        }
                    }
                }
            }
        }
        catch(ConcurrentModificationException cme){ }
        
        return animalList;
    }

    @Override
    public CModBlock getBlockAt(int x, int y, int z, int dimin, String WorldName) {
        World[] world = server.getWorld(WorldName);
        if(world != null){
            return new CModBlock(world[dimin].getBlockAt(x, y, z));
        }
        return null;
    }

    @Override
    public void setBlock(ICModBlock block) {
        World[] world = server.getWorld(block.getWorldName());
        if(world != null){
            world[block.getDimIndex()].setBlock((Block) block.getBlock());
        }
    }
    
    @Override
    public void setBlock(int type, int x, int y, int z, String worldname, int dim){
        World[] dims = server.getWorld(worldname);
        if(dims != null){
            dims[dim].setBlockAt(type, x, y, z);
        }
    }

    @Override
    public int getHighestBlockY(int x, int z, String worldname, int dim) {
        World[] dims = server.getWorld(worldname);
        if(dims != null){
            return dims[dim].getHighestBlockY(x, z);
        }
        return -1;
    }
    
    @Override
    public String getDefaultGroup(){
        return etc.getDataSource().getDefaultGroup().Name;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getAdminGroups(){
        List<String> admins = new ArrayList<String>();
        for(Group group : (List<Group>)etc.getDataSource().getGroupList()){
            if(group.Administrator){
                admins.add(group.Name);
            }
        }
        return admins;
    }
    
    @Override
    public Connection getCanarySQLConnection() throws SQLException{
        return etc.getConnection().getConnection();
    }
}
