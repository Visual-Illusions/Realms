import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModItem;
import net.visualillusionsent.viutils.ICModMob;
import net.visualillusionsent.viutils.ICModPlayer;
import net.visualillusionsent.viutils.ICModServer;

/**
 * CanaryMod-Server wrapper class
 * <p>
 * This class is a bridge to send Server data from the 'default package' to
 * 'packaged classes'.
 * <p>
 * This file is part of the VI Utilities Package (net.visualillusionsent.viutils)
 * 
 * @author Jason Jones
 * @see ICModServer
 */
public class CModServer implements ICModServer{
    private Server server;
    private CanaryConnection conn;
    
    public CModServer(Server server){
        this.server = server;
    }
    
    @Override
    public String getDefaultWorldName(){
        return server.getDefaultWorld().getName();   
    }

    @Override
    public List<ICModMob> getMobList() {
        List<Mob> daMobs;
        List<ICModMob> mobList = new ArrayList<ICModMob>();
        for(String worldname : server.getMCServer().worlds.keySet()){
            OWorldServer[] dimensions = server.getMCServer().worlds.get(worldname);
            for(int i = 0; i < 3; i++){
                World world = new World(dimensions[i]);
                daMobs = new ArrayList<Mob>(world.getMobList());
                Collections.copy(daMobs, world.getMobList());
                for(Mob mob : daMobs){
                    mobList.add(new CModMob(mob));
                }
            }
        }
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
        List<Mob> daAnimals;
        List<ICModMob> animalList = new ArrayList<ICModMob>();
        for(String worldname : server.getMCServer().worlds.keySet()){
            OWorldServer[] dimensions = server.getMCServer().worlds.get(worldname);
            for(int i = 0; i < 3; i++){
                World world = new World(dimensions[i]);
                daAnimals = new ArrayList<Mob>(world.getAnimalList());
                Collections.copy(daAnimals, world.getAnimalList());
                for(Mob animal : daAnimals){
                    animalList.add(new CModMob(animal));
                }
            }
        }
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
        this.conn = etc.getConnection();
        return conn.getConnection();
    }
    
    @Override
    public void releaseConn(){
        conn.release();
        conn = null;
    }

    @Override
    public ICModItem makeItem(int id, int amount, int slot, int damage) {
        return new CModItem(new Item(id, amount, slot, damage));
    }

    @Override
    public ICModPlayer getPlayer(String name) {
        return new CModPlayer(etc.getDataSource().getPlayer(name));
    }
}
