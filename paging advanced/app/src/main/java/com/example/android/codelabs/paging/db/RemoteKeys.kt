package com.example.android.codelabs.paging.db

import androidx.room.Entity
import androidx.room.PrimaryKey

// PagingState에서 마지막으로 로드된 항목을 가져와도 항목의 페이지의 인덱스는 알 수 없음
// 그렇기 떄문에, 따로 디비를 통해 저장해서 페이지 키값을 관리
@Entity(tableName = "remote_keys")
data class RemoteKeys (
    @PrimaryKey val repoId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
