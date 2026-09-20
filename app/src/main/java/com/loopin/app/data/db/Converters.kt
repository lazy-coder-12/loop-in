package com.loopin.app.data.db

import androidx.room.TypeConverter
import com.loopin.app.domain.model.BillingCycle
import com.loopin.app.domain.model.SubscriptionSource
import com.loopin.app.domain.model.SubscriptionStatus
import java.time.LocalDate

/**
 * Room TypeConverters for LocalDate and Domain Enums.
 */
class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromBillingCycle(cycle: BillingCycle?): String? {
        return cycle?.name
    }

    @TypeConverter
    fun toBillingCycle(cycleString: String?): BillingCycle? {
        return cycleString?.let { BillingCycle.valueOf(it) }
    }

    @TypeConverter
    fun fromSubscriptionStatus(status: SubscriptionStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toSubscriptionStatus(statusString: String?): SubscriptionStatus? {
        return statusString?.let { SubscriptionStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromSubscriptionSource(source: SubscriptionSource?): String? {
        return source?.name
    }

    @TypeConverter
    fun toSubscriptionSource(sourceString: String?): SubscriptionSource? {
        return sourceString?.let { SubscriptionSource.valueOf(it) }
    }
}
