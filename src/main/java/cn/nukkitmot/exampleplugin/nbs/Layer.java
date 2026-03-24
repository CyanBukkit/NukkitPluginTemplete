package cn.nukkitmot.exampleplugin.nbs;

import java.util.HashMap;

public class Layer {

    private final HashMap<Integer, Note> notes;
    private byte volume = 100;
    private String name = "";

    public Layer() {
        this.notes = new HashMap<>();
    }

    public HashMap<Integer, Note> getHashMap() {
        return notes;
    }

    public Note getNote(int tick) {
        return notes.get(tick);
    }

    public void setNote(int tick, Note note) {
        notes.put(tick, note);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }
}
