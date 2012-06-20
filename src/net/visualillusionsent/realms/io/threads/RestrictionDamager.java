package net.visualillusionsent.realms.io.threads;

import java.util.ConcurrentModificationException;
import java.util.List;

import net.visualillusionsent.realms.RHandle;
import net.visualillusionsent.realms.io.RLevel;
import net.visualillusionsent.realms.io.RealmsProps;
import net.visualillusionsent.realms.zones.Permission;
import net.visualillusionsent.realms.zones.Zone;
import net.visualillusionsent.realms.zones.ZoneLists;
import net.visualillusionsent.viutils.ICModPlayer;

/**
 * Realms Restricted
 * <p>
 * Called for players in a restricted zone and not authed
 * <p>
 * This file is part of Realms
 * 
 * @author darkdiplomat
 */
public class RestrictionDamager extends Thread{
    private RHandle rhandle;
    private String debug = "Player - Name: '%s' @ Location: X: '%s' Y: '%s' Z: '%s' World: '%s' Dimension: '%s'";
        
    /**
     * Class Constructor
     * 
     * @param Realm
     */
    public RestrictionDamager (RHandle rhandle) {
        this.rhandle = rhandle;
        this.setName("RestictionDamager-Thread");
        this.setDaemon(true);
    }
    
    /**
     * Realms Player Restricted Damage Run
     */
    public void run() {
        try{
            /*Player Lists*/
            List<ICModPlayer> restricted = ZoneLists.getInRestricted();
                
            /*Check Player List*/
            if (!restricted.isEmpty()){
                synchronized(restricted){
                    for(ICModPlayer thePlayer : restricted){
                        Zone zone = ZoneLists.getZone(rhandle.getEverywhere(thePlayer), thePlayer);
                        //Double Checks for Player can be hurt by the zone
                        if(!zone.permissionCheck(thePlayer, Permission.PermType.AUTHED) && !thePlayer.getMode() && !thePlayer.isDamageDisabled() && zone.getRestricted()){
                            //check that we can kill the player
                            if(!RealmsProps.getRestrictKills() && thePlayer.getHealth()-1 <= 0){
                                continue;
                            }
                            //Hurt Player
                            thePlayer.doDamage(2, 1);
                            
                            //Debugging message
                            rhandle.log(RLevel.PLAYER_EXPLODE, String.format(debug, thePlayer.getName(), Math.floor(thePlayer.getX()), Math.floor(thePlayer.getY()),
                                    Math.floor(thePlayer.getZ()), thePlayer.getWorldName(), thePlayer.getDimension()));
                        }
                    }
                }
            }
        }catch(ConcurrentModificationException CME){
            rhandle.log(RLevel.DEBUGWARNING, "Concurrent Modification Exception in RestrictedDamager. (Don't worry Not a major issue)");
        }
    }
}