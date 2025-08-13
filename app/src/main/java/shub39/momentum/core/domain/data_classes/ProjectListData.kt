package shub39.momentum.core.domain.data_classes

data class ProjectListData(
    val project: Project,
    val last10Days: List<Day>
)
