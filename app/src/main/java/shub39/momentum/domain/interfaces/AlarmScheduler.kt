package shub39.momentum.domain.interfaces

import shub39.momentum.domain.data_classes.Project

interface AlarmScheduler {
    fun schedule(project: Project)
    fun cancel(project: Project)
    fun cancelAll()
}