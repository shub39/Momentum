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