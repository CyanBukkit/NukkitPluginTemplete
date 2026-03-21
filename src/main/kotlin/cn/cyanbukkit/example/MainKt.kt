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

package cn.cyanbukkit.example

import cn.nukkitmot.exampleplugin.ExamplePlugin

object MainKt {

    fun run() {
        ExamplePlugin.instance.logger.info("Hello World!")
        // 输出真实的Kotlin版本并且 Kotlin加载了什么
        ExamplePlugin.instance.logger.info("Kotlin version: ${KotlinVersion.CURRENT}")
        // 输出Kotlin加载了什么
        ExamplePlugin.instance.logger.info("Kotlin loaded: ${KotlinVersion.CURRENT}")
        // ❤Kotlin
        ExamplePlugin.instance.logger.info("❤Kotlin")

    }

}