package net.visualillusionsent.realms.io;

import java.util.logging.Level;

public class RLevel extends Level{

    private static final long serialVersionUID = 210434042012L;
    
    public static final RLevel DEBUGINFO = new RLevel("DEBUGINFO", 6000);
    public static final RLevel DEBUGWARNING = new RLevel("DEBUGWARNING", 6010);
    public static final RLevel DEBUGSEVERE = new RLevel("DEBUGSEVERE", 6020);
    
    protected RLevel(String arg0, int arg1) {
        super(arg0, arg1);
    }

}
