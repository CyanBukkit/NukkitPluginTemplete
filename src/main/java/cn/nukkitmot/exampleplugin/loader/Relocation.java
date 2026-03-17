package cn.nukkitmot.exampleplugin.loader;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Relocation {
    private final String pattern;
    private final String relocatedPattern;
    private Collection<String> includes = Collections.emptyList();
    private Collection<String> excludes = Collections.emptyList();

    public Relocation(String pattern, String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }

    public static Collection<Relocation> parse(String[] relocations) {
        Collection<Relocation> result = new LinkedList<>();
        for (String relocation : relocations) {
            String[] parts = relocation.split("=");
            if (parts.length == 2) {
                result.add(new Relocation(parts[0], parts[1]));
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    public String getPattern() {
        return pattern;
    }

    public String getRelocatedPattern() {
        return relocatedPattern;
    }

    public Collection<String> getIncludes() {
        return includes;
    }

    public void setIncludes(Collection<String> includes) {
        this.includes = includes != null ? includes : Collections.emptyList();
    }

    public Collection<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Collection<String> excludes) {
        this.excludes = excludes != null ? excludes : Collections.emptyList();
    }
}
