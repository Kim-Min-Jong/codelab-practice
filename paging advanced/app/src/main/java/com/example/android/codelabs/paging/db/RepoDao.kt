package com.example.android.codelabs.paging.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.codelabs.paging.model.Repo

@Dao
interface RepoDao {

    // Repo 객체의 목록을 삽입
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<Repo>)

    // 이름 또는 설명에 쿼리 문자열이 포함된 저장소를 쿼리하고 결과를 별표 수의 내림차순으로 정렬한 후 이름의 가나다순으로 정렬 후 출력
    @Query("SELECT * FROM repos WHERE " +
        "name LIKE :queryString OR description LIKE :queryString " +
        "ORDER BY stars DESC, name DESC")
    fun reposByName(queryString: String): PagingSource<Int, Repo>

    // 전체 삭제
    @Query("DELETE FROM repos")
    suspend fun clearRepos()
}
