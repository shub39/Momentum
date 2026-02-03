package shub39.momentum.domain.data_classes

data class ProjectListData(
    val project: Project,
    val last10Days: List<Day>
)
