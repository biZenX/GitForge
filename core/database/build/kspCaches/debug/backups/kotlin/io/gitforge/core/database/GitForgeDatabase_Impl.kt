package io.gitforge.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GitForgeDatabase_Impl : GitForgeDatabase() {
  private val _repoDao: Lazy<RepoDao> = lazy {
    RepoDao_Impl(this)
  }

  private val _issueDao: Lazy<IssueDao> = lazy {
    IssueDao_Impl(this)
  }

  private val _workspaceDao: Lazy<WorkspaceDao> = lazy {
    WorkspaceDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "22494aa5d0047a94e2d1c21db7a301e7", "e80cf6f9c2922da1435e5db777a358e5") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `repos` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `fullName` TEXT NOT NULL, `ownerLogin` TEXT NOT NULL, `ownerAvatarUrl` TEXT NOT NULL, `description` TEXT, `isPrivate` INTEGER NOT NULL, `defaultBranch` TEXT NOT NULL, `stargazersCount` INTEGER NOT NULL, `forksCount` INTEGER NOT NULL, `language` TEXT, `updatedAt` TEXT, `lastCached` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `issues` (`id` INTEGER NOT NULL, `number` INTEGER NOT NULL, `repoFullName` TEXT NOT NULL, `title` TEXT NOT NULL, `body` TEXT, `state` TEXT NOT NULL, `authorLogin` TEXT NOT NULL, `authorAvatarUrl` TEXT NOT NULL, `commentsCount` INTEGER NOT NULL, `createdAt` TEXT NOT NULL, `updatedAt` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `workspaces` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `path` TEXT NOT NULL, `remoteUrl` TEXT, `currentBranch` TEXT NOT NULL, `lastAccessed` INTEGER NOT NULL, `isGitInitialized` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '22494aa5d0047a94e2d1c21db7a301e7')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `repos`")
        connection.execSQL("DROP TABLE IF EXISTS `issues`")
        connection.execSQL("DROP TABLE IF EXISTS `workspaces`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsRepos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRepos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("fullName", TableInfo.Column("fullName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("ownerLogin", TableInfo.Column("ownerLogin", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("ownerAvatarUrl", TableInfo.Column("ownerAvatarUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("description", TableInfo.Column("description", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("isPrivate", TableInfo.Column("isPrivate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("defaultBranch", TableInfo.Column("defaultBranch", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("stargazersCount", TableInfo.Column("stargazersCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("forksCount", TableInfo.Column("forksCount", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("language", TableInfo.Column("language", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRepos.put("lastCached", TableInfo.Column("lastCached", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRepos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRepos: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRepos: TableInfo = TableInfo("repos", _columnsRepos, _foreignKeysRepos,
            _indicesRepos)
        val _existingRepos: TableInfo = read(connection, "repos")
        if (!_infoRepos.equals(_existingRepos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |repos(io.gitforge.core.database.RepoEntity).
              | Expected:
              |""".trimMargin() + _infoRepos + """
              |
              | Found:
              |""".trimMargin() + _existingRepos)
        }
        val _columnsIssues: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsIssues.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("number", TableInfo.Column("number", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("repoFullName", TableInfo.Column("repoFullName", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("body", TableInfo.Column("body", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("state", TableInfo.Column("state", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("authorLogin", TableInfo.Column("authorLogin", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("authorAvatarUrl", TableInfo.Column("authorAvatarUrl", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("commentsCount", TableInfo.Column("commentsCount", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsIssues.put("updatedAt", TableInfo.Column("updatedAt", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysIssues: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesIssues: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoIssues: TableInfo = TableInfo("issues", _columnsIssues, _foreignKeysIssues,
            _indicesIssues)
        val _existingIssues: TableInfo = read(connection, "issues")
        if (!_infoIssues.equals(_existingIssues)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |issues(io.gitforge.core.database.IssueEntity).
              | Expected:
              |""".trimMargin() + _infoIssues + """
              |
              | Found:
              |""".trimMargin() + _existingIssues)
        }
        val _columnsWorkspaces: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWorkspaces.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkspaces.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkspaces.put("path", TableInfo.Column("path", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkspaces.put("remoteUrl", TableInfo.Column("remoteUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkspaces.put("currentBranch", TableInfo.Column("currentBranch", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkspaces.put("lastAccessed", TableInfo.Column("lastAccessed", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWorkspaces.put("isGitInitialized", TableInfo.Column("isGitInitialized", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWorkspaces: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWorkspaces: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWorkspaces: TableInfo = TableInfo("workspaces", _columnsWorkspaces,
            _foreignKeysWorkspaces, _indicesWorkspaces)
        val _existingWorkspaces: TableInfo = read(connection, "workspaces")
        if (!_infoWorkspaces.equals(_existingWorkspaces)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |workspaces(io.gitforge.core.database.WorkspaceEntity).
              | Expected:
              |""".trimMargin() + _infoWorkspaces + """
              |
              | Found:
              |""".trimMargin() + _existingWorkspaces)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "repos", "issues", "workspaces")
  }

  public override fun clearAllTables() {
    super.performClear(false, "repos", "issues", "workspaces")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(RepoDao::class, RepoDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(IssueDao::class, IssueDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WorkspaceDao::class, WorkspaceDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun repoDao(): RepoDao = _repoDao.value

  public override fun issueDao(): IssueDao = _issueDao.value

  public override fun workspaceDao(): WorkspaceDao = _workspaceDao.value
}
