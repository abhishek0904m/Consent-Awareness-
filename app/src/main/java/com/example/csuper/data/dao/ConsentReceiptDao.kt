package com.example.csuper.data.dao

import androidx.room.*
import com.example.csuper.data.ConsentReceiptEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for consent receipts
 */
@Dao
interface ConsentReceiptDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(receipt: ConsentReceiptEntity): Long
    
    @Query("SELECT * FROM consent_receipts ORDER BY timestamp DESC")
    fun getAllReceipts(): Flow<List<ConsentReceiptEntity>>
    
    @Query("SELECT * FROM consent_receipts WHERE permissionName = :permissionName ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReceipt(permissionName: String): ConsentReceiptEntity?
    
    @Query("DELETE FROM consent_receipts")
    suspend fun deleteAll()
}
