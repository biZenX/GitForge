package io.gitforge.core.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RepoDao_Impl(
  __db: RoomDatabase,
) : RepoDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRepoEntity: EntityInsertAdapter<RepoEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRepoEntity = object : EntityInsertAdapter<RepoEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `repos` (`id`,`name`,`fullName`,`ownerLogin`,`ownerAvatarUrl`,`description`,`isPrivate`,`defaultBranch`,`stargazersCount`,`forksCount`,`language`,`updatedAt`,`lastCached`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RepoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.fullName)
        statement.bindText(4, entity.ownerLogin)
        statement.bindText(5, entity.ownerAvatarUrl)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDescription)
        }
        val _tmp: Int = if (entity.isPrivate) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindText(8, entity.defaultBranch)
        statement.bindLong(9, entity.stargazersCount.toLong())
        statement.bindLong(10, entity.forksCount.toLong())
        val _tmpLanguage: String? = entity.language
        if (_tmpLanguage == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpLanguage)
        }
        val _tmpUpdatedAt: String? = entity.updatedAt
        if (_tmpUpdatedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpUpdatedAt)
        }
        statement.bindLong(13, entity.lastCached)
      }
    }
  }

  public override suspend fun insertRepos(repos: List<RepoEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfRepoEntity.insert(_connection, repos)
  }

  public override fun getCachedRepos(): Flow<List<RepoEntity>> {
    val _sql: String = "SELECT * FROM repos ORDER BY lastCached DESC"
    return createFlow(__db, false, arrayOf("repos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfOwnerLogin: Int = getColumnIndexOrThrow(_stmt, "ownerLogin")
        val _columnIndexOfOwnerAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "ownerAvatarUrl")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIsPrivate: Int = getColumnIndexOrThrow(_stmt, "isPrivate")
        val _columnIndexOfDefaultBranch: Int = getColumnIndexOrThrow(_stmt, "defaultBranch")
        val _columnIndexOfStargazersCount: Int = getColumnIndexOrThrow(_stmt, "stargazersCount")
        val _columnIndexOfForksCount: Int = getColumnIndexOrThrow(_stmt, "forksCount")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastCached: Int = getColumnIndexOrThrow(_stmt, "lastCached")
        val _result: MutableList<RepoEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RepoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFullName: String
          _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          val _tmpOwnerLogin: String
          _tmpOwnerLogin = _stmt.getText(_columnIndexOfOwnerLogin)
          val _tmpOwnerAvatarUrl: String
          _tmpOwnerAvatarUrl = _stmt.getText(_columnIndexOfOwnerAvatarUrl)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpIsPrivate: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrivate).toInt()
          _tmpIsPrivate = _tmp != 0
          val _tmpDefaultBranch: String
          _tmpDefaultBranch = _stmt.getText(_columnIndexOfDefaultBranch)
          val _tmpStargazersCount: Int
          _tmpStargazersCount = _stmt.getLong(_columnIndexOfStargazersCount).toInt()
          val _tmpForksCount: Int
          _tmpForksCount = _stmt.getLong(_columnIndexOfForksCount).toInt()
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          val _tmpUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          }
          val _tmpLastCached: Long
          _tmpLastCached = _stmt.getLong(_columnIndexOfLastCached)
          _item =
              RepoEntity(_tmpId,_tmpName,_tmpFullName,_tmpOwnerLogin,_tmpOwnerAvatarUrl,_tmpDescription,_tmpIsPrivate,_tmpDefaultBranch,_tmpStargazersCount,_tmpForksCount,_tmpLanguage,_tmpUpdatedAt,_tmpLastCached)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRepoByFullName(fullName: String): RepoEntity? {
    val _sql: String = "SELECT * FROM repos WHERE fullName = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, fullName)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfOwnerLogin: Int = getColumnIndexOrThrow(_stmt, "ownerLogin")
        val _columnIndexOfOwnerAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "ownerAvatarUrl")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIsPrivate: Int = getColumnIndexOrThrow(_stmt, "isPrivate")
        val _columnIndexOfDefaultBranch: Int = getColumnIndexOrThrow(_stmt, "defaultBranch")
        val _columnIndexOfStargazersCount: Int = getColumnIndexOrThrow(_stmt, "stargazersCount")
        val _columnIndexOfForksCount: Int = getColumnIndexOrThrow(_stmt, "forksCount")
        val _columnIndexOfLanguage: Int = getColumnIndexOrThrow(_stmt, "language")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfLastCached: Int = getColumnIndexOrThrow(_stmt, "lastCached")
        val _result: RepoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFullName: String
          _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          val _tmpOwnerLogin: String
          _tmpOwnerLogin = _stmt.getText(_columnIndexOfOwnerLogin)
          val _tmpOwnerAvatarUrl: String
          _tmpOwnerAvatarUrl = _stmt.getText(_columnIndexOfOwnerAvatarUrl)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpIsPrivate: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrivate).toInt()
          _tmpIsPrivate = _tmp != 0
          val _tmpDefaultBranch: String
          _tmpDefaultBranch = _stmt.getText(_columnIndexOfDefaultBranch)
          val _tmpStargazersCount: Int
          _tmpStargazersCount = _stmt.getLong(_columnIndexOfStargazersCount).toInt()
          val _tmpForksCount: Int
          _tmpForksCount = _stmt.getLong(_columnIndexOfForksCount).toInt()
          val _tmpLanguage: String?
          if (_stmt.isNull(_columnIndexOfLanguage)) {
            _tmpLanguage = null
          } else {
            _tmpLanguage = _stmt.getText(_columnIndexOfLanguage)
          }
          val _tmpUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmpUpdatedAt = null
          } else {
            _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          }
          val _tmpLastCached: Long
          _tmpLastCached = _stmt.getLong(_columnIndexOfLastCached)
          _result =
              RepoEntity(_tmpId,_tmpName,_tmpFullName,_tmpOwnerLogin,_tmpOwnerAvatarUrl,_tmpDescription,_tmpIsPrivate,_tmpDefaultBranch,_tmpStargazersCount,_tmpForksCount,_tmpLanguage,_tmpUpdatedAt,_tmpLastCached)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearRepos() {
    val _sql: String = "DELETE FROM repos"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
