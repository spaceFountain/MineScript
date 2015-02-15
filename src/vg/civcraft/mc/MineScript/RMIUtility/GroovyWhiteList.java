package vg.civcraft.mc.MineScript.RMIUtility;

import groovy.lang.Closure;
import groovy.lang.Script;
import org.kohsuke.groovy.sandbox.GroovyValueFilter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by isaac on 2/12/15.
 */
public class GroovyWhiteList extends GroovyValueFilter {
    private Set<Class> allowed = new HashSet<Class>();

    public GroovyWhiteList() {
        allowed.add(Boolean.class);
        allowed.add(Integer.class);
        allowed.add(Long.class);
        allowed.add(String.class);
        allowed.add(Character.class);
        allowed.add(Double.class);
    }

    public void allow(Class toAllow){
        allowed.add(toAllow);
    }

    @Override
    public Object filter(Object o) {
        if (o == null) {
            return o;
        } else if (allowed.contains(o.getClass())) {
            return o;
        } else if (o instanceof Closure || o instanceof Script){
            return o;
        } else {
            throw new SecurityException("Illegal type " + o.getClass());
        }
    }
}
