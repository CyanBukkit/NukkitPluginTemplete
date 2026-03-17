package cn.nukkitmot.exampleplugin.loader;

import cn.nukkit.plugin.Plugin;

import java.net.HttpURLConnection;
import java.net.URL;

public class KotlinBootstrap {

    private static NukkitLibraryManager libraryManager;
    private static String repositoryUrl;

    public static void init(Plugin plugin) {
        libraryManager = new NukkitLibraryManager(plugin);
        
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();
            repositoryUrl = Repositories.MAVEN_CENTRAL;
        } catch (Exception e) {
            repositoryUrl = "https://maven.aliyun.com/repository/central";
        }
        
        libraryManager.addRepository(repositoryUrl);
    }

    public static void loadKotlin(String groupId, String artifactId, String version) {
        if (libraryManager == null) {
            throw new IllegalStateException("KotlinBootstrap.init() must be called first");
        }
        
        libraryManager.loadLibrary(Library.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version)
                .build());
    }

    public static void load(String groupId, String artifactId, String version) {
        if (libraryManager == null) {
            throw new IllegalStateException("KotlinBootstrap.init() must be called first");
        }
        
        libraryManager.loadLibrary(Library.builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version)
                .build());
    }

    public static LibraryManager getLibraryManager() {
        return libraryManager;
    }
}
