package com.loopin.app.detection

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.loopin.app.data.repository.SubscriptionRepository
import com.loopin.app.detection.model.ParsedDebitAlert
import com.loopin.app.detection.parser.RbiNotificationParser
import com.loopin.app.detection.samples.SampleDebitAlert
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.Subscription
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Orchestrator for RBI pre-debit notification detection, permission status,
 * confirmation flows, and building subscription records locally on-device.
 */
class DetectionManager(
    private val context: Context,
    private val repository: SubscriptionRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _isListenerEnabled = MutableStateFlow(false)
    val isListenerEnabled: StateFlow<Boolean> = _isListenerEnabled.asStateFlow()

    private val _activeAlertForConfirmation = MutableStateFlow<ParsedDebitAlert?>(null)
    val activeAlertForConfirmation: StateFlow<ParsedDebitAlert?> = _activeAlertForConfirmation.asStateFlow()

    private val _recentAlerts = MutableStateFlow<List<ParsedDebitAlert>>(emptyList())
    val recentAlerts: StateFlow<List<ParsedDebitAlert>> = _recentAlerts.asStateFlow()

    init {
        checkPermission()
    }

    /**
     * Checks if the user has enabled NotificationListenerService access for Loop'in.
     */
    fun checkPermission(): Boolean {
        return try {
            val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
            val isEnabled = enabledPackages.contains(context.packageName)
            _isListenerEnabled.value = isEnabled
            isEnabled
        } catch (_: Exception) {
            _isListenerEnabled.value = false
            false
        }
    }

    /**
     * Opens Android System Settings directly to Notification Access so the user can enable Loop'in.
     */
    fun openNotificationListenerSettings() {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    /**
     * Processes incoming notification text on-device.
     */
    fun processNotification(
        packageName: String,
        title: String,
        text: String,
        postTime: Long = System.currentTimeMillis()
    ): ParsedDebitAlert? {
        val parsed = RbiNotificationParser.parseNotification(
            packageName = packageName,
            title = title,
            text = text,
            postTime = postTime
        ) ?: return null

        _recentAlerts.value = listOf(parsed) + _recentAlerts.value.take(19)
        _activeAlertForConfirmation.value = parsed
        return parsed
    }

    /**
     * Simulates an RBI pre-debit notification for instant testing/demo without waiting for a bank SMS/push.
     */
    fun simulateAlert(sample: SampleDebitAlert): ParsedDebitAlert? {
        return processNotification(
            packageName = sample.packageName,
            title = sample.title,
            text = sample.body
        )
    }

    fun dismissActiveAlert() {
        _activeAlertForConfirmation.value = null
    }

    /**
     * Builds and stores a subscription record into Room DB with source = AUTO.
     * If a subscription for this service already exists, its due date is updated without duplicating.
     */
    suspend fun confirmAlertAndBuildSubscription(
        alert: ParsedDebitAlert,
        customName: String? = null,
        customCategory: String? = null,
        customCycle: BillingCycle? = null
    ): Long {
        val name = customName?.takeIf { it.isNotBlank() } ?: alert.matchedServiceName
        val category = customCategory ?: alert.category
        val cycle = customCycle ?: alert.billingCycle

        val existing = repository.getSubscriptionByName(name)
        val id = if (existing != null) {
            val updated = existing.copy(
                amountMinor = alert.amountMinor,
                nextDueDate = alert.debitDate,
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = category,
                billingCycle = cycle
            )
            repository.insertSubscription(updated)
            updated.id
        } else {
            val newSub = Subscription(
                name = name,
                amountMinor = alert.amountMinor,
                billingCycle = cycle,
                nextDueDate = alert.debitDate,
                status = SubscriptionStatus.ACTIVE,
                source = SubscriptionSource.AUTO,
                category = category
            )
            repository.insertSubscription(newSub)
        }

        _activeAlertForConfirmation.value = null
        return id
    }
}
