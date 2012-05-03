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
    private String debug = "Player - Name: '%s' @ Location: X: '%d' Y: '%d' Z: '%d' World: '%s' Dimension: '%d'";
        
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
                    for(ICModPlayer thePlayer : scanList){
                        Zone myZone = ZoneLists.getZone(rhandle.getEverywhere(thePlayer), thePlayer);
                        //Checks for Player can be hurt by the Explosion
                        if(thePlayer.getWorldName().equals(block.getWorldName()) && !thePlayer.getMode() && !myZone.getSanctuary() && !thePlayer.isDamageDisabled()){
                            int bx = block.getX(), by = block.getY(), bz = block.getZ();
                            for(int x = (bx-5); x < (bx+6); x++){
                                for(int y = (by-5); y < (by+6); y++){
                                    for(int z = (bz-5); z < (bz+6); z++){
                                        if(thePlayer.getX() == x && thePlayer.getY() == y && thePlayer.getZ() == z){
                                            //Hurt Player based on proximity
                                            if(x == (bx-5) || y == (by-5) || z == (bz-5) || x == (bx+5) || y == (by+5) || z == (bz+5)){
                                                thePlayer.doDamage(1, 2);
                                            }
                                            else if(x == (bx-4) || y == (by-4) || z == (bz-4) || x == (bx+4) || y == (by+4) || z == (bz+4)){
                                                thePlayer.doDamage(1, 4);
                                            }
                                            else if(x == (bx-3) || y == (by-3) || z == (bz-3) || x == (bx+3) || y == (by+3) || z == (bz+3)){
                                                thePlayer.doDamage(1, 6);
                                            }
                                            else if(x == (bx-2) || y == (by-2) || z == (bz-2) || x == (bx+2) || y == (by+2) || z == (bz+2)){
                                                thePlayer.doDamage(1, 8);
                                            }
                                            else if(x == (bx-1) || y == (by-1) || z == (bz-1) || x == (bx+1) || y == (by+1) || z == (bz+1)){
                                                thePlayer.doDamage(1, 10);
                                            }
                                            else if(x == bx || y == by || z == bz){
                                                thePlayer.doDamage(1, 12);
                                            }
                                            
                                            //Debugging Message
                                            rhandle.log(RLevel.PLAYER_EXPLODE, String.format(debug, thePlayer.getName(), Math.floor(thePlayer.getX()), Math.floor(thePlayer.getY()),
                                                    Math.floor(thePlayer.getZ()), thePlayer.getWorldName(), thePlayer.getDimension()));
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
