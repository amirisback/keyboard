package com.frogobox.appkeyboard.data.local.autotext

/**
 * Created by Faisal Amir on 10/03/23
 * https://github.com/amirisback
 */

import androidx.room.*
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface AutoTextDao {

    @Insert
    fun insert(autoText: AutoTextEntity) : Completable

    @Insert
    fun insert(autoTexts: List<AutoTextEntity>) : Completable

    @Update
    fun update(autoText: AutoTextEntity) : Completable

    @Delete
    fun delete(autoText: AutoTextEntity) : Completable

    @Query("DELETE FROM auto_text WHERE id in (:idList)")
    fun delete(idList: List<Int>): Completable

    @Query("DELETE FROM auto_text")
    fun nukeData(): Completable

    @Query("SELECT * FROM auto_text ORDER BY id ASC")
    fun getAll() : Single<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE id = :id")
    fun getById(id: Int) : Single<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE title LIKE '%' || :search || '%' ORDER BY id ASC")
    fun getByTitle(search: String): Single<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE label = :search ORDER BY id ASC")
    fun getByLabel(search: AutoTextLabel) : Single<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE body LIKE '%' || :search || '%' ORDER BY id ASC")
    fun getByBody(search: String) : Single<List<AutoTextEntity>>

    @Query("SELECT * FROM auto_text WHERE (title LIKE '%' || :search || '%' OR body LIKE '%' || :search || '%') ORDER BY id ASC")
    fun getByTitleOrBody(search: String): Single<List<AutoTextEntity>>

}