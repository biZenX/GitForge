package io.gitforge.core.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
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
public class IssueDao_Impl(
  __db: RoomDatabase,
) : IssueDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfIssueEntity: EntityInsertAdapter<IssueEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfIssueEntity = object : EntityInsertAdapter<IssueEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `issues` (`id`,`number`,`repoFullName`,`title`,`body`,`state`,`authorLogin`,`authorAvatarUrl`,`commentsCount`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: IssueEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.number.toLong())
        statement.bindText(3, entity.repoFullName)
        statement.bindText(4, entity.title)
        val _tmpBody: String? = entity.body
        if (_tmpBody == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpBody)
        }
        statement.bindText(6, entity.state)
        statement.bindText(7, entity.authorLogin)
        statement.bindText(8, entity.authorAvatarUrl)
        statement.bindLong(9, entity.commentsCount.toLong())
        statement.bindText(10, entity.createdAt)
        statement.bindText(11, entity.updatedAt)
      }
    }
  }

  public override suspend fun insertIssues(issues: List<IssueEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfIssueEntity.insert(_connection, issues)
  }

  public override fun getIssuesForRepo(repoFullName: String): Flow<List<IssueEntity>> {
    val _sql: String = "SELECT * FROM issues WHERE repoFullName = ? ORDER BY number DESC"
    return createFlow(__db, false, arrayOf("issues")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, repoFullName)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNumber: Int = getColumnIndexOrThrow(_stmt, "number")
        val _columnIndexOfRepoFullName: Int = getColumnIndexOrThrow(_stmt, "repoFullName")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfBody: Int = getColumnIndexOrThrow(_stmt, "body")
        val _columnIndexOfState: Int = getColumnIndexOrThrow(_stmt, "state")
        val _columnIndexOfAuthorLogin: Int = getColumnIndexOrThrow(_stmt, "authorLogin")
        val _columnIndexOfAuthorAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "authorAvatarUrl")
        val _columnIndexOfCommentsCount: Int = getColumnIndexOrThrow(_stmt, "commentsCount")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<IssueEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: IssueEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNumber: Int
          _tmpNumber = _stmt.getLong(_columnIndexOfNumber).toInt()
          val _tmpRepoFullName: String
          _tmpRepoFullName = _stmt.getText(_columnIndexOfRepoFullName)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpBody: String?
          if (_stmt.isNull(_columnIndexOfBody)) {
            _tmpBody = null
          } else {
            _tmpBody = _stmt.getText(_columnIndexOfBody)
          }
          val _tmpState: String
          _tmpState = _stmt.getText(_columnIndexOfState)
          val _tmpAuthorLogin: String
          _tmpAuthorLogin = _stmt.getText(_columnIndexOfAuthorLogin)
          val _tmpAuthorAvatarUrl: String
          _tmpAuthorAvatarUrl = _stmt.getText(_columnIndexOfAuthorAvatarUrl)
          val _tmpCommentsCount: Int
          _tmpCommentsCount = _stmt.getLong(_columnIndexOfCommentsCount).toInt()
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: String
          _tmpUpdatedAt = _stmt.getText(_columnIndexOfUpdatedAt)
          _item =
              IssueEntity(_tmpId,_tmpNumber,_tmpRepoFullName,_tmpTitle,_tmpBody,_tmpState,_tmpAuthorLogin,_tmpAuthorAvatarUrl,_tmpCommentsCount,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearIssuesForRepo(repoFullName: String) {
    val _sql: String = "DELETE FROM issues WHERE repoFullName = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, repoFullName)
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
