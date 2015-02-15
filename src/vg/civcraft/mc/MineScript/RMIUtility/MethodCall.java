package vg.civcraft.mc.MineScript.RMIUtility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by isaac on 2/12/15.
 */
public class MethodCall {
    private Object object;
    private Method method;
    private Object[] parameters;

    public MethodCall(Class type, String method, Object... parameters) {
        this.object = null;
        this.parameters = parameters;

        Class[] types = new Class[parameters.length];
        int i = 0;
        for (Object parameter: parameters) {
            Class temp = parameter.getClass();
            types[i++] = temp;
        }


        try {
            this.method = type.getMethod(method, types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            this.method = null;
        }

    }

    public MethodCall(Class type, String method, Class[] types,  Object... parameters) {
        this.object = null;
        this.parameters = parameters;

        try {
            this.method = type.getMethod(method, types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            this.method = null;
        }
    }

    public MethodCall(Object object, String method, Class[] types,  Object... parameters) {
        this.object = object;
        this.parameters = parameters;

        try {
            this.method = object.getClass().getMethod(method, types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            this.method = null;
        }

    }


    public MethodCall(Object object, Method method, Object... parameters) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
    }

    public MethodCall(Object object, String method, Object... parameters) {
        this.object = object;
        this.parameters = parameters;

        Class[] types = new Class[parameters.length];
        int i = 0;
        for (Object parameter: parameters) {
            Class temp = parameter.getClass();
            types[i++] = temp;
        }


        try {
            this.method = object.getClass().getMethod(method, types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            this.method = null;
        }

    }

    public void invoke() {
        if (method == null) {
            return;
        }

        try {
            method.invoke(object, parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        }
    }
}
