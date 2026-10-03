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
package shub39.momentum.core.data_classes

@JvmInline
value class AnalyticsEvent(val name: String) {
    companion object {
        val APP_OPENED = AnalyticsEvent("app_opened")
        val PAYWALL_OPENED = AnalyticsEvent("paywall_opened")
        val PAYWALL_PURCHASED = AnalyticsEvent("paywall_purchased")
        val SETTINGS_OPENED = AnalyticsEvent("settings_opened")
        val BACKUP_CREATED = AnalyticsEvent("backup_created")
        val BACKUP_RESTORED = AnalyticsEvent("backup_restored")
        val ABOUT_OPENED = AnalyticsEvent("about_opened")
        val CHANGELOG_OPENED = AnalyticsEvent("changelog_opened")
        val PROJECT_CREATED = AnalyticsEvent("project_created")
        val PROJECT_DELETED = AnalyticsEvent("project_deleted")
        val PROJECT_SELECTED = AnalyticsEvent("project_selected")
        val DAY_ADDED = AnalyticsEvent("day_added")
        val DAY_DELETED = AnalyticsEvent("day_deleted")
        val MONTAGE_CREATED = AnalyticsEvent("montage_created")
        val MONTAGE_CONFIG_UPDATED = AnalyticsEvent("montage_config_updated")
        val REMINDER_UPDATED = AnalyticsEvent("reminder_updated")
        val FACE_SCAN_STARTED = AnalyticsEvent("face_scan_started")
        val ONBOARDING_COMPLETED = AnalyticsEvent("onboarding_completed")
        val CAMERA_PHOTO_TAKEN = AnalyticsEvent("camera_photo_taken")
        val THEME_CHANGED = AnalyticsEvent("theme_changed")
    }
}
