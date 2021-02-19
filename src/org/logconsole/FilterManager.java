package org.logconsole;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

public class FilterManager {
    private ArrayList<Filter> filters;
    private boolean active;

    public FilterManager() {
        filters = new ArrayList<Filter>();
    }

    public synchronized void addFilter(String regex, boolean enabled) throws PatternSyntaxException {
        filters.add(new Filter(regex, enabled));
        active = true;
    }

    public synchronized void removeAllFilters() {
        filters.clear();
        active = false;
    }

    public synchronized ArrayList<Filter> getFilters() {
        return filters;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized boolean isFiltered(CharSequence s) {
        for (Filter f : filters) {
            if (f.enabled) {
                Matcher m = f.pattern.matcher(s);
                if (m.matches())
                    return true;
            }
        }
        return false;
    }

    public class Filter {
        String regex;
        Pattern pattern;
        boolean enabled;

        public Filter(String regex, boolean enabled) throws PatternSyntaxException {
            this.regex = regex;
            this.enabled = enabled;
            if (!regex.startsWith("^"))
                regex = ".*" + regex;
            if (!regex.endsWith("$"))
                regex = regex + ".*";
            pattern = Pattern.compile(regex);
        }
    }
}