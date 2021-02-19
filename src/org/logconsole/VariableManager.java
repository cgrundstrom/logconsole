package org.logconsole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class VariableManager {
    private HashMap<String, Variable> variableMap;

    public VariableManager() {
        variableMap = new HashMap<String, Variable>();
        clear();
    }

    public synchronized void addVariable(String name, String value) {
        variableMap.put(name, new Variable(name, value));
    }

    public synchronized void clear() {
        variableMap.clear();
        variableMap.put("yyyy", new DateVariable("yyyy"));
        variableMap.put("yy", new DateVariable("yy"));
        variableMap.put("mm", new DateVariable("MM"));
        variableMap.put("dd", new DateVariable("dd"));
        variableMap.put("hh", new DateVariable("HH"));
    }

    public synchronized ArrayList<Variable> getVariableList() {
        ArrayList<Variable> list = new ArrayList<Variable>();
        for (Variable variable : variableMap.values())
            if (!(variable instanceof DateVariable))
                list.add(variable);
        Collections.sort(list, new Comparator<Variable>() {
            public int compare(Variable v1, Variable v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });
        return list;
    }

    public String expandVariables(String name) {
        return expandVariables(name, 0);
    }

    private String expandVariables(String string, int depth) {
        if (string == null || depth >= 10)
            return string;

        StringBuffer b = new StringBuffer();
        String s = string;
        while (true) {
            int idx1 = s.indexOf('{');
            if (idx1 == -1) {
                b.append(s);
                break;
            }
            int idx2 = s.indexOf('}', idx1 + 1);
            if (idx2 == -1) {
                b.append(s);
                break;
            }
            String prefix = s.substring(0, idx1);
            String name = s.substring(idx1 + 1, idx2);
            String suffix = s.substring(idx2 + 1);

            b.append(prefix);
            Variable v = variableMap.get(name);
            if (v == null)
                b.append('{').append(name).append('}');
            else
                b.append(v.getValue());
            s = suffix;
        }
        return b.toString();
    }

    public class Variable {
        private String name;
        private String value;

        public Variable(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public class DateVariable extends Variable {
        private SimpleDateFormat format;

        public DateVariable(String name) {
            super(name, null);
            format = new SimpleDateFormat(name);
        }

        public String getValue() {
            return format.format(new Date());
        }
    }

    public static void main(String[] args) {
        VariableManager manager = new VariableManager();

        System.out.println(manager.expandVariables("/t/logs/catalina.{yyyy}-{mm}-{dd}.log"));
        System.out.println(manager.expandVariables("/t/logs/catalina.{yy}-{mm}-{dd}.log"));

        manager.addVariable("foo", "bar");
        System.out.println(manager.expandVariables("{foo}"));
        System.out.println(manager.expandVariables("x{foo}x"));
        System.out.println(manager.expandVariables("{foot}"));
        System.out.println(manager.expandVariables("{foo}{foot}"));
        System.out.println(manager.expandVariables("{foot}{foo}"));
    }
}