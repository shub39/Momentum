/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
val generateChangelogJson by
    tasks.registering {
        val inputFile = rootProject.file("CHANGELOG.md")
        val outputDir = file("$projectDir/src/main/assets/")
        val outputFile = File(outputDir, "changelog.json")

        inputs.file(inputFile)
        outputs.file(outputFile)

        doLast {
            if (!outputDir.exists()) outputDir.mkdirs()

            val lines = inputFile.readLines()

            val map = mutableMapOf<String, MutableList<String>>()
            var currentVersion: String? = null

            for (line in lines) {
                when {
                    line.startsWith("## ") -> {
                        currentVersion = line.removePrefix("## ").trim()
                        map[currentVersion] = mutableListOf()
                    }

                    line.startsWith("- ") && currentVersion != null -> {
                        map[currentVersion]?.add(line.removePrefix("- ").trim())
                    }
                }
            }

            val json = buildString {
                append("[\n")

                map.entries.forEachIndexed { index, entry ->
                    append("  {\n")
                    append("    \"version\": \"${entry.key}\",\n")
                    append("    \"changes\": [\n")

                    entry.value.forEachIndexed { i, item ->
                        append("      \"${item.replace("\"", "\\\"")}\"")
                        if (i != entry.value.lastIndex) append(",")
                        append("\n")
                    }

                    append("    ]\n")
                    append("  }")

                    if (index != map.entries.size - 1) append(",")
                    append("\n")
                }

                append("]")
            }

            outputFile.writeText(json)
        }
    }

tasks.named("preBuild") { dependsOn(generateChangelogJson) }
