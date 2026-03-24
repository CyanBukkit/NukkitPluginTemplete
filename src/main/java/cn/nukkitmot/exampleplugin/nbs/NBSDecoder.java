package cn.nukkitmot.exampleplugin.nbs;

import cn.nukkit.plugin.PluginLogger;

import java.io.*;
import java.util.HashMap;

public class NBSDecoder {

    private static PluginLogger logger;

    public static void setLogger(PluginLogger log) {
        logger = log;
    }

    public static Song parse(File file) {
        try {
            return parse(new FileInputStream(file), file);
        } catch (FileNotFoundException e) {
            if (logger != null) {
                logger.error("NBS file not found: " + file.getName(), e);
            }
        }
        return null;
    }

    private static Song parse(InputStream inputStream, File file) {
        HashMap<Integer, Layer> layerHashMap = new HashMap<>();
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
            short length = readShort(dis);
            short songHeight = readShort(dis);
            String title = readString(dis);
            String author = readString(dis);
            readString(dis);
            String description = readString(dis);
            float speed = readShort(dis) / 100f;
            dis.readBoolean();
            dis.readByte();
            dis.readByte();
            readInt(dis);
            readInt(dis);
            readInt(dis);
            readInt(dis);
            readInt(dis);
            readString(dis);
            short tick = -1;
            while (true) {
                short jumpTicks = readShort(dis);
                if (jumpTicks == 0) {
                    break;
                }
                tick += jumpTicks;
                short layer = -1;
                while (true) {
                    short jumpLayers = readShort(dis);
                    if (jumpLayers == 0) {
                        break;
                    }
                    layer += jumpLayers;
                    setNote(layer, tick, dis.readByte(), dis.readByte(), layerHashMap);
                }
            }
            for (int i = 0; i < songHeight; i++) {
                Layer l = layerHashMap.get(i);
                if (l != null) {
                    l.setName(readString(dis));
                    l.setVolume(dis.readByte());
                }
            }
            return new Song(speed, layerHashMap, songHeight, length, title, author, description, file);
        } catch (IOException e) {
            if (logger != null) {
                logger.error("Failed to parse NBS file: " + file.getName(), e);
            }
        }
        return null;
    }

    private static void setNote(int layer, int tick, byte instrument, byte key, HashMap<Integer, Layer> layerHashMap) {
        Layer l = layerHashMap.get(layer);
        if (l == null) {
            l = new Layer();
            layerHashMap.put(layer, l);
        }
        l.setNote(tick, new Note(instrument, key));
    }

    private static short readShort(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        return (short) (byte1 + (byte2 << 8));
    }

    private static int readInt(DataInputStream dis) throws IOException {
        int byte1 = dis.readUnsignedByte();
        int byte2 = dis.readUnsignedByte();
        int byte3 = dis.readUnsignedByte();
        int byte4 = dis.readUnsignedByte();
        return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
    }

    private static String readString(DataInputStream dis) throws IOException {
        int length = readInt(dis);
        StringBuilder sb = new StringBuilder(length);
        for (; length > 0; --length) {
            char c = (char) dis.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
