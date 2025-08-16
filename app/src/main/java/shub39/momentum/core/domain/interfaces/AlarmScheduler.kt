package shub39.momentum.core.domain.interfaces

import shub39.momentum.core.domain.data_classes.Project

interface AlarmScheduler {
    fun schedule(project: Project)
    fun cancel(project: Project)
    fun cancelAll()
}