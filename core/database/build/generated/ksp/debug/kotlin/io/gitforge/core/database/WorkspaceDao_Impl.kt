package io.gitforge.core.database

import androidx.room.EntityDeleteOrUpdateAdapter
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
public class WorkspaceDao_Impl(
  __db: RoomDatabase,
) : WorkspaceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWorkspaceEntity: EntityInsertAdapter<WorkspaceEntity>

  private val __deleteAdapterOfWorkspaceEntity: EntityDeleteOrUpdateAdapter<WorkspaceEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWorkspaceEntity = object : EntityInsertAdapter<WorkspaceEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `workspaces` (`id`,`name`,`path`,`remoteUrl`,`currentBranch`,`lastAccessed`,`isGitInitialized`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WorkspaceEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.path)
        val _tmpRemoteUrl: String? = entity.remoteUrl
        if (_tmpRemoteUrl == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpRemoteUrl)
        }
        statement.bindText(5, entity.currentBranch)
        statement.bindLong(6, entity.lastAccessed)
        val _tmp: Int = if (entity.isGitInitialized) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
    this.__deleteAdapterOfWorkspaceEntity = object : EntityDeleteOrUpdateAdapter<WorkspaceEntity>()
        {
      protected override fun createQuery(): String = "DELETE FROM `workspaces` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: WorkspaceEntity) {
        statement.bindText(1, entity.id)
      }
    }
  }

  public override suspend fun insertWorkspace(workspace: WorkspaceEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfWorkspaceEntity.insert(_connection, workspace)
  }

  public override suspend fun deleteWorkspace(workspace: WorkspaceEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfWorkspaceEntity.handle(_connection, workspace)
  }

  public override fun getAllWorkspaces(): Flow<List<WorkspaceEntity>> {
    val _sql: String = "SELECT * FROM workspaces ORDER BY lastAccessed DESC"
    return createFlow(__db, false, arrayOf("workspaces")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPath: Int = getColumnIndexOrThrow(_stmt, "path")
        val _columnIndexOfRemoteUrl: Int = getColumnIndexOrThrow(_stmt, "remoteUrl")
        val _columnIndexOfCurrentBranch: Int = getColumnIndexOrThrow(_stmt, "currentBranch")
        val _columnIndexOfLastAccessed: Int = getColumnIndexOrThrow(_stmt, "lastAccessed")
        val _columnIndexOfIsGitInitialized: Int = getColumnIndexOrThrow(_stmt, "isGitInitialized")
        val _result: MutableList<WorkspaceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WorkspaceEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPath: String
          _tmpPath = _stmt.getText(_columnIndexOfPath)
          val _tmpRemoteUrl: String?
          if (_stmt.isNull(_columnIndexOfRemoteUrl)) {
            _tmpRemoteUrl = null
          } else {
            _tmpRemoteUrl = _stmt.getText(_columnIndexOfRemoteUrl)
          }
          val _tmpCurrentBranch: String
          _tmpCurrentBranch = _stmt.getText(_columnIndexOfCurrentBranch)
          val _tmpLastAccessed: Long
          _tmpLastAccessed = _stmt.getLong(_columnIndexOfLastAccessed)
          val _tmpIsGitInitialized: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsGitInitialized).toInt()
          _tmpIsGitInitialized = _tmp != 0
          _item =
              WorkspaceEntity(_tmpId,_tmpName,_tmpPath,_tmpRemoteUrl,_tmpCurrentBranch,_tmpLastAccessed,_tmpIsGitInitialized)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getWorkspaceById(id: String): WorkspaceEntity? {
    val _sql: String = "SELECT * FROM workspaces WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPath: Int = getColumnIndexOrThrow(_stmt, "path")
        val _columnIndexOfRemoteUrl: Int = getColumnIndexOrThrow(_stmt, "remoteUrl")
        val _columnIndexOfCurrentBranch: Int = getColumnIndexOrThrow(_stmt, "currentBranch")
        val _columnIndexOfLastAccessed: Int = getColumnIndexOrThrow(_stmt, "lastAccessed")
        val _columnIndexOfIsGitInitialized: Int = getColumnIndexOrThrow(_stmt, "isGitInitialized")
        val _result: WorkspaceEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPath: String
          _tmpPath = _stmt.getText(_columnIndexOfPath)
          val _tmpRemoteUrl: String?
          if (_stmt.isNull(_columnIndexOfRemoteUrl)) {
            _tmpRemoteUrl = null
          } else {
            _tmpRemoteUrl = _stmt.getText(_columnIndexOfRemoteUrl)
          }
          val _tmpCurrentBranch: String
          _tmpCurrentBranch = _stmt.getText(_columnIndexOfCurrentBranch)
          val _tmpLastAccessed: Long
          _tmpLastAccessed = _stmt.getLong(_columnIndexOfLastAccessed)
          val _tmpIsGitInitialized: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsGitInitialized).toInt()
          _tmpIsGitInitialized = _tmp != 0
          _result =
              WorkspaceEntity(_tmpId,_tmpName,_tmpPath,_tmpRemoteUrl,_tmpCurrentBranch,_tmpLastAccessed,_tmpIsGitInitialized)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteWorkspaceById(id: String) {
    val _sql: String = "DELETE FROM workspaces WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
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
