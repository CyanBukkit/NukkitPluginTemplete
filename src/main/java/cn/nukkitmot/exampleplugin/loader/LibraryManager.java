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

import java.nio.file.Path;
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
}
