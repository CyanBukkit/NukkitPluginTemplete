package cn.nukkitmot.exampleplugin.loader.lanternmc;

import java.io.File;
import java.nio.file.Path;

public class LibraryManager {
    private final Path cachePath;
    private final String directoryName;

    public LibraryManager(Path cachePath, String directoryName) {
        this.cachePath = cachePath;
        this.directoryName = directoryName;
    }

    public LibraryManager(Path cachePath) {
        this(cachePath, "libs");
    }

    public void loadLibrary(Library library) {
    }

    protected void addToClasspath(Path file) {
    }

    public Path getLibraryPath(Library library) {
        String path = library.getGroupId().replace(".", "/") + "/" 
                    + library.getArtifactId() + "/" 
                    + library.getVersion() + "/" 
                    + library.getArtifactId() + "-" + library.getVersion() + ".jar";
        return cachePath.resolve(directoryName).resolve(path);
    }

    public static class Library {
        private final String groupId;
        private final String artifactId;
        private final String version;

        private Library(String groupId, String artifactId, String version) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
        }

        public static Library create(String groupId, String artifactId, String version) {
            return new Library(groupId, artifactId, version);
        }

        public String getGroupId() { return groupId; }
        public String getArtifactId() { return artifactId; }
        public String getVersion() { return version; }
    }
}
