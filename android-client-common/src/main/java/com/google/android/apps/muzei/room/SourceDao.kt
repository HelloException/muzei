/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.muzei.room

import android.content.ComponentName
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.google.android.apps.muzei.room.converter.ComponentNameTypeConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Dao for Sources
 */
@Dao
abstract class SourceDao {

    @get:Query("SELECT * FROM sources")
    abstract val sources: LiveData<List<Source>>

    @Query("SELECT * FROM sources")
    abstract suspend fun getSources(): List<Source>

    @get:TypeConverters(ComponentNameTypeConverter::class)
    @get:Query("SELECT component_name FROM sources")
    abstract val sourceComponentNamesBlocking: List<ComponentName>

    @get:Query("SELECT * FROM sources WHERE selected=1 ORDER BY component_name")
    abstract val currentSource: LiveData<Source?>

    @get:Query("SELECT * FROM sources WHERE selected=1 ORDER BY component_name")
    abstract val currentSourceBlocking: Source?

    suspend fun getCurrentSource() = withContext(Dispatchers.Default) {
        currentSourceBlocking
    }

    @Query("SELECT * FROM sources WHERE selected=1 AND wantsNetworkAvailable=1")
    abstract suspend fun getCurrentSourcesThatWantNetwork(): List<Source>

    @Insert
    abstract fun insert(source: Source)

    @TypeConverters(ComponentNameTypeConverter::class)
    @Query("SELECT component_name FROM sources WHERE component_name LIKE :packageName || '%'")
    abstract fun getSourcesComponentNamesByPackageNameBlocking(packageName: String): List<ComponentName>

    @TypeConverters(ComponentNameTypeConverter::class)
    @Query("SELECT * FROM sources WHERE component_name = :componentName")
    abstract fun getSourceByComponentNameBlocking(componentName: ComponentName): Source?

    suspend fun getSourceByComponentName(
            componentName: ComponentName
    ) = withContext(Dispatchers.Default) {
        getSourceByComponentNameBlocking(componentName)
    }
    @Update
    abstract fun update(source: Source)

    @Delete
    abstract fun delete(source: Source)

    @TypeConverters(ComponentNameTypeConverter::class)
    @Query("DELETE FROM sources WHERE component_name IN (:componentNames)")
    abstract fun deleteAll(componentNames: Array<ComponentName>)
}
