package vg.civcraft.mc.MineScript;

import vg.civcraft.mc.MineScript.apis.Logger;
import vg.civcraft.mc.MineScript.scriptDispatcher.RemoteScript;
import vg.civcraft.mc.MineScript.scriptDispatcher.RemoteScriptStarter;

import java.rmi.Naming;

/**
 * Created by isaac on 2/13/15.
 */
public class Tester {
    public static void main(String[] args) throws Exception{
        RemoteScriptStarter starter = (RemoteScriptStarter) Naming.lookup("//localhost/ScriptStarter");
        int id = starter.start("logger.info(\"it works\")", 1000, 2000);
        RemoteScript script;
        while ((script = starter.getScript(id)) == null){
            Thread.sleep(500);
        }
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("Tester"); ;
        script.addAPI(new Logger(logger), "logger");
        script.start();
        script.exit();
    }
}
