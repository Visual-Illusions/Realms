package net.visualillusionsent.realms.runnables;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModBlock;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Player Explosion Damage
 * <p>
 * Called if player damage from explosions is enabled
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class RealmsPlayerExplosionDamage extends Thread{
    private RHandle rhandle;
    private List<ICModPlayer> scanList;
    private ICModBlock block;
        
    /**
     * class constructor
     *  
     * @param Realm
     * @param RLocation
     */
    public RealmsPlayerExplosionDamage (RHandle rhandle, ICModBlock block) {
        this.rhandle = rhandle;
        this.block = block;
        scanList = new ArrayList<ICModPlayer>();
        setName("RealmsPlayerExplosionDamage-Thread");
    }
    
    /**
     * Realms Player Explosion Damage Run
     */
    public void run() {
        try{
            /*Player Lists*/
            scanList = rhandle.getServer().getPlayerList();

            /*Check Player List*/
            if (!scanList.isEmpty()){
                synchronized(scanList){
                    for(ICModPlayer player : scanList){
                        Zone myZone = ZoneLists.getZone(rhandle.getEverywhere(player), player);
                        //Checks for Player can be hurt by the Explosion
                        if(player.getWorldName().equals(block.getWorldName()) && !player.getMode() && !myZone.getSanctuary() && !player.isDamageDisabled()){
                            int bx = block.getX(), by = block.getY(), bz = block.getZ();
                            for(int x = (bx-5); x < (bx+6); x++){
                                for(int y = (by-5); y < (by+6); y++){
                                    for(int z = (bz-5); z < (bz+6); z++){
                                        if(player.getX() == x && player.getY() == y && player.getZ() == z){
                                            //Hurt Player based on proximity
                                            if(x == (bx-5) || y == (by-5) || z == (bz-5) || x == (bx+5) || y == (by+5) || z == (bz+5)){
                                                player.doDamage(2);
                                            }
                                            if(x == (bx-4) || y == (by-4) || z == (bz-4) || x == (bx+4) || y == (by+4) || z == (bz+4)){
                                                player.doDamage(4);
                                            }
                                            if(x == (bx-3) || y == (by-3) || z == (bz-3) || x == (bx+3) || y == (by+3) || z == (bz+3)){
                                                player.doDamage(6);
                                            }
                                            if(x == (bx-2) || y == (by-2) || z == (bz-2) || x == (bx+2) || y == (by+2) || z == (bz+2)){
                                                player.doDamage(8);
                                            }
                                            if(x == (bx-1) || y == (by-1) || z == (bz-1) || x == (bx+1) || y == (by+1) || z == (bz+1)){
                                                player.doDamage(10);
                                            }
                                            if(x == bx || y == by || z == bz){
                                                player.doDamage(12);
                                            }
                                            if(player.getHealth() <= 0){
                                                //If explosion kills player drop their inventory
                                                player.dropInventory();
                                            }
                                            rhandle.log(RLevel.DEBUGINFO, "ExplosionPlayerDamage - Player: '" + player.getName()+ 
                                                                          "' at Location - X: '"+Math.floor(player.getX())+"' Y: '"+Math.floor(player.getY())+"' Z: '"+Math.floor(player.getZ())+
                                                                          "' World: '"+player.getWorldName()+"' Dimension: '"+ player.getDimension()+"'");//Debugging
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in RPEDThread. (Don't worry Not a major issue)");
        }
    }
}
