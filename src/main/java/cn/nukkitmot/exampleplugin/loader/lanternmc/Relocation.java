package cn.nukkitmot.exampleplugin.loader.lanternmc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Relocation {
    private final String pattern;
    private final String relocatedPattern;

    public Relocation(String pattern, String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }

    public String getPattern() {
        return pattern;
    }

    public String getRelocatedPattern() {
        return relocatedPattern;
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
}
