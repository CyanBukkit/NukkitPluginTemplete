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
import java.util.Collections;
import java.util.LinkedList;

public record Relocation(String pattern, String relocatedPattern) {

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
