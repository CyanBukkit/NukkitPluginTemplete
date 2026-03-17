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

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

public class Library {
    private final Collection<String> urls;
    private final Collection<String> repositories;
    private final String id;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String classifier;
    private final byte[] checksum;
    private final Collection<Relocation> relocations;
    private final boolean isolatedLoad;

    private Library(Collection<String> urls, Collection<String> repositories, String id,
                    String groupId, String artifactId, String version, String classifier,
                    byte[] checksum, Collection<Relocation> relocations, boolean isolatedLoad) {
        this.urls = urls;
        this.repositories = repositories;
        this.id = id;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.classifier = classifier;
        this.checksum = checksum;
        this.relocations = relocations;
        this.isolatedLoad = isolatedLoad;
    }

    public static Library create(String groupId, String artifactId, String version) {
        return builder()
                .groupId(groupId)
                .artifactId(artifactId)
                .version(version)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<String> getUrls() { return urls; }
    public Collection<String> getRepositories() { return repositories; }
    public String getId() { return id; }
    public String getGroupId() { return groupId; }
    public String getArtifactId() { return artifactId; }
    public String getVersion() { return version; }
    public String getClassifier() { return classifier; }
    public byte[] getChecksum() { return checksum; }
    public Collection<Relocation> getRelocations() { return relocations; }
    public boolean isIsolatedLoad() { return isolatedLoad; }

    public static class Builder {
        private final Collection<String> urls = new LinkedList<>();
        private final Collection<String> repositories = new LinkedList<>();
        private String id;
        private String groupId;
        private String artifactId;
        private String version;
        private String classifier;
        private byte[] checksum;
        private boolean isolatedLoad;
        private final Collection<Relocation> relocations = new LinkedList<>();

        public Builder url(String url) {
            this.urls.add(url);
            return this;
        }

        public Builder repository(String url) {
            this.repositories.add(url.endsWith("/") ? url : url + "/");
            return this;
        }

        public Builder id(String id) {
            this.id = id != null ? id : UUID.randomUUID().toString();
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder artifactId(String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder classifier(String classifier) {
            this.classifier = classifier;
            return this;
        }

        public Builder checksum(byte[] checksum) {
            this.checksum = checksum;
            return this;
        }

        public Builder checksum(String checksum) {
            return this.checksum(java.util.Base64.getDecoder().decode(checksum));
        }

        public Builder isolatedLoad(boolean isolatedLoad) {
            this.isolatedLoad = isolatedLoad;
            return this;
        }

        public Builder relocate(Relocation relocation) {
            this.relocations.add(relocation);
            return this;
        }

        public Builder relocate(String pattern, String relocatedPattern) {
            return this.relocate(new Relocation(pattern, relocatedPattern));
        }

        public Library build() {
            return new Library(urls, repositories, id, groupId, artifactId, version,
                    classifier, checksum, relocations, isolatedLoad);
        }
    }
}
