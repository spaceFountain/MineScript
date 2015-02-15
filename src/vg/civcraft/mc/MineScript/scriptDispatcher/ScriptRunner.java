package vg.civcraft.mc.MineScript.scriptDispatcher;

import groovy.lang.*;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import vg.civcraft.mc.MineScript.RMIUtility.GroovyWhiteList;
import vg.civcraft.mc.MineScript.RMIUtility.RequestQueue;
import vg.civcraft.mc.MineScript.apis.RemoteAPI;

/**
 * Created by isaac on 2/12/15.
 */
public class ScriptRunner extends Thread{
    public RequestQueue queue = new RequestQueue();

    private CompilerConfiguration compilerConfiguration = new CompilerConfiguration().
            addCompilationCustomizers(new SandboxTransformer());
    private Binding binding = new Binding();
    private GroovyShell shell = new GroovyShell(binding, compilerConfiguration);
    private GroovyWhiteList whiteList = new GroovyWhiteList();

    private String code;

    public ScriptRunner() {
        initSandBox();
    }

    public void loadCode(String code) {
        this.code = code;
    }

    public void allowAndBind(RemoteAPI api, String name) {
        System.out.println("added api "+name);
        whiteList.allow(api.getClass());
        binding.setVariable(name, api);
    }

    public void allow(Class<? extends RemoteAPI> api) {
        whiteList.allow(api.getClass());
    }

    public void runScript() {
        System.out.println("starting code");
        shell.evaluate(code);
    }

    @Override
    public void run(){
        while (true) loop();
    }

    public void loop() {
        queue.waitAndInvoke();
    }


    private void initSandBox() {
        whiteList.register();
    }
}
