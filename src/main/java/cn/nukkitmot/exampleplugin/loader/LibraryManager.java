/*
 * Copyright (c) 2026.
 * # 太霄玉府五雷使院镇煞符
 * # 敕令：诸 BUG 急退，急急如律令！
 * #
 * # 雷  火  雷
 * #    部  令
 * # 雷  火  雷
 * #
 * # 净天地神咒（节选）
 * # 天地自然，秽气分散，洞中玄虚，晃朗太元；
 * # 八方威神，使我自然，灵宝符命，普告九天。
 * #
 * # 本代码受太上老君、九天应元雷声普化天尊庇佑，
 * # 如生 BUG，则坎离交泰，雷火丹成，BUG 自化虚无。
 *
 */

package cn.nukkitmot.exampleplugin.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryManager {
    private final Path cachePath;
    private final String directoryName;
    private final List<String> repositories = new ArrayList<>();

    public LibraryManager(Path cachePath, String directoryName) {
        this.cachePath = cachePath;
        this.directoryName = directoryName;
    }

    public LibraryManager(Path cachePath) {
        this(cachePath, "libs");
    }

    public LibraryManager addRepository(String repository) {
        if (!repositories.contains(repository)) {
            repositories.add(repository.endsWith("/") ? repository : repository + "/");
        }
        return this;
    }

    public List<String> getRepositories() {
        return Collections.unmodifiableList(repositories);
    }

    public void loadLibrary(Library library) {
        Path libraryPath = getLibraryPath(library);

        if (!Files.exists(libraryPath)) {
            downloadLibrary(library, libraryPath);
        }

        verifyChecksum(libraryPath, library.getChecksum());

        addToClasspath(libraryPath);
    }

    protected void addToClasspath(Path file) {
    }

    protected void downloadLibrary(Library library, Path targetPath) {
        try {
            Files.createDirectories(targetPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create library directory: " + targetPath.getParent(), e);
        }

        URL libraryUrl = resolveLibraryUrl(library);
        if (libraryUrl == null) {
            throw new RuntimeException("Could not resolve library URL for: " + library);
        }

        try (InputStream inputStream = libraryUrl.openStream()) {
            Files.copy(inputStream, targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download library: " + libraryUrl, e);
        }
    }

    protected URL resolveLibraryUrl(Library library) {
        if (!library.getUrls().isEmpty()) {
            for (String url : library.getUrls()) {
                try {
                    return new URL(url);
                } catch (Exception ignored) {
                }
            }
        }

        List<String> repos = new ArrayList<>(library.getRepositories());
        if (repos.isEmpty()) {
            repos.addAll(getRepositories());
        }
        if (repos.isEmpty()) {
            repos.add(Repositories.MAVEN_CENTRAL);
        }

        String artifactPath = buildArtifactPath(library);

        for (String repo : repos) {
            try {
                String urlStr = repo + artifactPath;
                URL url = new URL(urlStr);
                if (isUrlAccessible(url)) {
                    return url;
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private boolean isUrlAccessible(URL url) {
        try (InputStream inputStream = url.openStream()) {
            return inputStream.read() != -1;
        } catch (Exception e) {
            return false;
        }
    }

    protected String buildArtifactPath(Library library) {
        StringBuilder path = new StringBuilder();
        path.append(library.getGroupId().replace(".", "/"));
        path.append("/");
        path.append(library.getArtifactId());
        path.append("/");
        path.append(library.getVersion());
        path.append("/");
        path.append(library.getArtifactId());
        path.append("-");
        path.append(library.getVersion());

        if (library.getClassifier() != null && !library.getClassifier().isEmpty()) {
            path.append("-").append(library.getClassifier());
        }

        path.append(".jar");

        return path.toString();
    }

    protected void verifyChecksum(Path libraryPath, byte[] expectedChecksum) {
        if (expectedChecksum == null || expectedChecksum.length == 0) {
            return;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] actualChecksum = digest.digest(Files.readAllBytes(libraryPath));

            if (!MessageDigest.isEqual(expectedChecksum, actualChecksum)) {
                throw new SecurityException("Library checksum verification failed: " + libraryPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify library checksum: " + libraryPath, e);
        }
    }

    public Path getCachePath() {
        return cachePath;
    }

    public Path getLibraryPath(Library library) {
        String path = library.getGroupId().replace(".", "/") + "/" 
                    + library.getArtifactId() + "/" 
                    + library.getVersion() + "/" 
                    + library.getArtifactId() + "-" + library.getVersion() + ".jar";
        return cachePath.resolve(directoryName).resolve(path);
    }
}
