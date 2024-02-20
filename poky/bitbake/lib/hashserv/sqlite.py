#! /usr/bin/env python3
#
# Copyright (C) 2023 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#
import sqlite3
import logging
from contextlib import closing
from . import User

logger = logging.getLogger("hashserv.sqlite")

UNIHASH_TABLE_DEFINITION = (
    ("method", "TEXT NOT NULL", "UNIQUE"),
    ("taskhash", "TEXT NOT NULL", "UNIQUE"),
    ("unihash", "TEXT NOT NULL", ""),
    ("gc_mark", "TEXT NOT NULL", ""),
)

UNIHASH_TABLE_COLUMNS = tuple(name for name, _, _ in UNIHASH_TABLE_DEFINITION)

OUTHASH_TABLE_DEFINITION = (
    ("method", "TEXT NOT NULL", "UNIQUE"),
    ("taskhash", "TEXT NOT NULL", "UNIQUE"),
    ("outhash", "TEXT NOT NULL", "UNIQUE"),
    ("created", "DATETIME", ""),
    # Optional fields
    ("owner", "TEXT", ""),
    ("PN", "TEXT", ""),
    ("PV", "TEXT", ""),
    ("PR", "TEXT", ""),
    ("task", "TEXT", ""),
    ("outhash_siginfo", "TEXT", ""),
)

OUTHASH_TABLE_COLUMNS = tuple(name for name, _, _ in OUTHASH_TABLE_DEFINITION)

USERS_TABLE_DEFINITION = (
    ("username", "TEXT NOT NULL", "UNIQUE"),
    ("token", "TEXT NOT NULL", ""),
    ("permissions", "TEXT NOT NULL", ""),
)

USERS_TABLE_COLUMNS = tuple(name for name, _, _ in USERS_TABLE_DEFINITION)


CONFIG_TABLE_DEFINITION = (
    ("name", "TEXT NOT NULL", "UNIQUE"),
    ("value", "TEXT", ""),
)

CONFIG_TABLE_COLUMNS = tuple(name for name, _, _ in CONFIG_TABLE_DEFINITION)


def _make_table(cursor, name, definition):
    cursor.execute(
        """
        CREATE TABLE IF NOT EXISTS {name} (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            {fields}
            UNIQUE({unique})
            )
        """.format(
            name=name,
            fields=" ".join("%s %s," % (name, typ) for name, typ, _ in definition),
            unique=", ".join(
                name for name, _, flags in definition if "UNIQUE" in flags
            ),
        )
    )


def map_user(row):
    if row is None:
        return None
    return User(
        username=row["username"],
        permissions=set(row["permissions"].split()),
    )


def _make_condition_statement(columns, condition):
    where = {}
    for c in columns:
        if c in condition and condition[c] is not None:
            where[c] = condition[c]

    return where, " AND ".join("%s=:%s" % (k, k) for k in where.keys())


def _get_sqlite_version(cursor):
    cursor.execute("SELECT sqlite_version()")

    version = []
    for v in cursor.fetchone()[0].split("."):
        try:
            version.append(int(v))
        except ValueError:
            version.append(v)

    return tuple(version)


def _schema_table_name(version):
    if version >= (3, 33):
        return "sqlite_schema"

    return "sqlite_master"


class DatabaseEngine(object):
    def __init__(self, dbname, sync):
        self.dbname = dbname
        self.logger = logger
        self.sync = sync

    async def create(self):
        db = sqlite3.connect(self.dbname)
        db.row_factory = sqlite3.Row

        with closing(db.cursor()) as cursor:
            _make_table(cursor, "unihashes_v3", UNIHASH_TABLE_DEFINITION)
            _make_table(cursor, "outhashes_v2", OUTHASH_TABLE_DEFINITION)
            _make_table(cursor, "users", USERS_TABLE_DEFINITION)
            _make_table(cursor, "config", CONFIG_TABLE_DEFINITION)

            cursor.execute("PRAGMA journal_mode = WAL")
            cursor.execute(
                "PRAGMA synchronous = %s" % ("NORMAL" if self.sync else "OFF")
            )

            # Drop old indexes
            cursor.execute("DROP INDEX IF EXISTS taskhash_lookup")
            cursor.execute("DROP INDEX IF EXISTS outhash_lookup")
            cursor.execute("DROP INDEX IF EXISTS taskhash_lookup_v2")
            cursor.execute("DROP INDEX IF EXISTS outhash_lookup_v2")
            cursor.execute("DROP INDEX IF EXISTS taskhash_lookup_v3")

            # TODO: Upgrade from tasks_v2?
            cursor.execute("DROP TABLE IF EXISTS tasks_v2")

            # Create new indexes
            cursor.execute(
                "CREATE INDEX IF NOT EXISTS taskhash_lookup_v4 ON unihashes_v3 (method, taskhash)"
            )
            cursor.execute(
                "CREATE INDEX IF NOT EXISTS unihash_lookup_v1 ON unihashes_v3 (unihash)"
            )
            cursor.execute(
                "CREATE INDEX IF NOT EXISTS outhash_lookup_v3 ON outhashes_v2 (method, outhash)"
            )
            cursor.execute("CREATE INDEX IF NOT EXISTS config_lookup ON config (name)")

            sqlite_version = _get_sqlite_version(cursor)

            cursor.execute(
                f"""
                SELECT name FROM {_schema_table_name(sqlite_version)} WHERE type = 'table' AND name = 'unihashes_v2'
                """
            )
            if cursor.fetchone():
                self.logger.info("Upgrading Unihashes V2 -> V3...")
                cursor.execute(
                    """
                    INSERT INTO unihashes_v3 (id, method, unihash, taskhash, gc_mark)
                    SELECT id, method, unihash, taskhash, '' FROM unihashes_v2
                    """
                )
                cursor.execute("DROP TABLE unihashes_v2")
                db.commit()
                self.logger.info("Upgrade complete")

    def connect(self, logger):
        return Database(logger, self.dbname, self.sync)


class Database(object):
    def __init__(self, logger, dbname, sync):
        self.dbname = dbname
        self.logger = logger

        self.db = sqlite3.connect(self.dbname)
        self.db.row_factory = sqlite3.Row

        with closing(self.db.cursor()) as cursor:
            cursor.execute("PRAGMA journal_mode = WAL")
            cursor.execute(
                "PRAGMA synchronous = %s" % ("NORMAL" if sync else "OFF")
            )

            self.sqlite_version = _get_sqlite_version(cursor)

    async def __aenter__(self):
        return self

    async def __aexit__(self, exc_type, exc_value, traceback):
        await self.close()

    async def _set_config(self, cursor, name, value):
        cursor.execute(
            """
            INSERT OR REPLACE INTO config (id, name, value) VALUES
            ((SELECT id FROM config WHERE name=:name), :name, :value)
            """,
            {
                "name": name,
                "value": value,
            },
        )

    async def _get_config(self, cursor, name):
        cursor.execute(
            "SELECT value FROM config WHERE name=:name",
            {
                "name": name,
            },
        )
        row = cursor.fetchone()
        if row is None:
            return None
        return row["value"]

    async def close(self):
        self.db.close()

    async def get_unihash_by_taskhash_full(self, method, taskhash):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT *, unihashes_v3.unihash AS unihash FROM outhashes_v2
                INNER JOIN unihashes_v3 ON unihashes_v3.method=outhashes_v2.method AND unihashes_v3.taskhash=outhashes_v2.taskhash
                WHERE outhashes_v2.method=:method AND outhashes_v2.taskhash=:taskhash
                ORDER BY outhashes_v2.created ASC
                LIMIT 1
                """,
                {
                    "method": method,
                    "taskhash": taskhash,
                },
            )
            return cursor.fetchone()

    async def get_unihash_by_outhash(self, method, outhash):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT *, unihashes_v3.unihash AS unihash FROM outhashes_v2
                INNER JOIN unihashes_v3 ON unihashes_v3.method=outhashes_v2.method AND unihashes_v3.taskhash=outhashes_v2.taskhash
                WHERE outhashes_v2.method=:method AND outhashes_v2.outhash=:outhash
                ORDER BY outhashes_v2.created ASC
                LIMIT 1
                """,
                {
                    "method": method,
                    "outhash": outhash,
                },
            )
            return cursor.fetchone()

    async def unihash_exists(self, unihash):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT * FROM unihashes_v3 WHERE unihash=:unihash
                LIMIT 1
                """,
                {
                    "unihash": unihash,
                },
            )
            return cursor.fetchone() is not None

    async def get_outhash(self, method, outhash):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT * FROM outhashes_v2
                WHERE outhashes_v2.method=:method AND outhashes_v2.outhash=:outhash
                ORDER BY outhashes_v2.created ASC
                LIMIT 1
                """,
                {
                    "method": method,
                    "outhash": outhash,
                },
            )
            return cursor.fetchone()

    async def get_equivalent_for_outhash(self, method, outhash, taskhash):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT outhashes_v2.taskhash AS taskhash, unihashes_v3.unihash AS unihash FROM outhashes_v2
                INNER JOIN unihashes_v3 ON unihashes_v3.method=outhashes_v2.method AND unihashes_v3.taskhash=outhashes_v2.taskhash
                -- Select any matching output hash except the one we just inserted
                WHERE outhashes_v2.method=:method AND outhashes_v2.outhash=:outhash AND outhashes_v2.taskhash!=:taskhash
                -- Pick the oldest hash
                ORDER BY outhashes_v2.created ASC
                LIMIT 1
                """,
                {
                    "method": method,
                    "outhash": outhash,
                    "taskhash": taskhash,
                },
            )
            return cursor.fetchone()

    async def get_equivalent(self, method, taskhash):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                "SELECT taskhash, method, unihash FROM unihashes_v3 WHERE method=:method AND taskhash=:taskhash",
                {
                    "method": method,
                    "taskhash": taskhash,
                },
            )
            return cursor.fetchone()

    async def remove(self, condition):
        def do_remove(columns, table_name, cursor):
            where, clause = _make_condition_statement(columns, condition)
            if where:
                query = f"DELETE FROM {table_name} WHERE {clause}"
                cursor.execute(query, where)
                return cursor.rowcount

            return 0

        count = 0
        with closing(self.db.cursor()) as cursor:
            count += do_remove(OUTHASH_TABLE_COLUMNS, "outhashes_v2", cursor)
            count += do_remove(UNIHASH_TABLE_COLUMNS, "unihashes_v3", cursor)
            self.db.commit()

        return count

    async def get_current_gc_mark(self):
        with closing(self.db.cursor()) as cursor:
            return await self._get_config(cursor, "gc-mark")

    async def gc_status(self):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT COUNT() FROM unihashes_v3 WHERE
                    gc_mark=COALESCE((SELECT value FROM config WHERE name='gc-mark'), '')
                """
            )
            keep_rows = cursor.fetchone()[0]

            cursor.execute(
                """
                SELECT COUNT() FROM unihashes_v3 WHERE
                    gc_mark!=COALESCE((SELECT value FROM config WHERE name='gc-mark'), '')
                """
            )
            remove_rows = cursor.fetchone()[0]

            current_mark = await self._get_config(cursor, "gc-mark")

            return (keep_rows, remove_rows, current_mark)

    async def gc_mark(self, mark, condition):
        with closing(self.db.cursor()) as cursor:
            await self._set_config(cursor, "gc-mark", mark)

            where, clause = _make_condition_statement(UNIHASH_TABLE_COLUMNS, condition)

            new_rows = 0
            if where:
                cursor.execute(
                    f"""
                    UPDATE unihashes_v3 SET
                        gc_mark=COALESCE((SELECT value FROM config WHERE name='gc-mark'), '')
                    WHERE {clause}
                    """,
                    where,
                )
                new_rows = cursor.rowcount

            self.db.commit()
            return new_rows

    async def gc_sweep(self):
        with closing(self.db.cursor()) as cursor:
            # NOTE: COALESCE is not used in this query so that if the current
            # mark is NULL, nothing will happen
            cursor.execute(
                """
                DELETE FROM unihashes_v3 WHERE
                    gc_mark!=(SELECT value FROM config WHERE name='gc-mark')
                """
            )
            count = cursor.rowcount
            await self._set_config(cursor, "gc-mark", None)

            self.db.commit()
            return count

    async def clean_unused(self, oldest):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                DELETE FROM outhashes_v2 WHERE created<:oldest AND NOT EXISTS (
                    SELECT unihashes_v3.id FROM unihashes_v3 WHERE unihashes_v3.method=outhashes_v2.method AND unihashes_v3.taskhash=outhashes_v2.taskhash LIMIT 1
                )
                """,
                {
                    "oldest": oldest,
                },
            )
            self.db.commit()
            return cursor.rowcount

    async def insert_unihash(self, method, taskhash, unihash):
        with closing(self.db.cursor()) as cursor:
            prevrowid = cursor.lastrowid
            cursor.execute(
                """
                INSERT OR IGNORE INTO unihashes_v3 (method, taskhash, unihash, gc_mark) VALUES
                    (
                    :method,
                    :taskhash,
                    :unihash,
                    COALESCE((SELECT value FROM config WHERE name='gc-mark'), '')
                    )
                """,
                {
                    "method": method,
                    "taskhash": taskhash,
                    "unihash": unihash,
                },
            )
            self.db.commit()
            return cursor.lastrowid != prevrowid

    async def insert_outhash(self, data):
        data = {k: v for k, v in data.items() if k in OUTHASH_TABLE_COLUMNS}
        keys = sorted(data.keys())
        query = "INSERT OR IGNORE INTO outhashes_v2 ({fields}) VALUES({values})".format(
            fields=", ".join(keys),
            values=", ".join(":" + k for k in keys),
        )
        with closing(self.db.cursor()) as cursor:
            prevrowid = cursor.lastrowid
            cursor.execute(query, data)
            self.db.commit()
            return cursor.lastrowid != prevrowid

    def _get_user(self, username):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                SELECT username, permissions, token FROM users WHERE username=:username
                """,
                {
                    "username": username,
                },
            )
            return cursor.fetchone()

    async def lookup_user_token(self, username):
        row = self._get_user(username)
        if row is None:
            return None, None
        return map_user(row), row["token"]

    async def lookup_user(self, username):
        return map_user(self._get_user(username))

    async def set_user_token(self, username, token):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                UPDATE users SET token=:token WHERE username=:username
                """,
                {
                    "username": username,
                    "token": token,
                },
            )
            self.db.commit()
            return cursor.rowcount != 0

    async def set_user_perms(self, username, permissions):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                UPDATE users SET permissions=:permissions WHERE username=:username
                """,
                {
                    "username": username,
                    "permissions": " ".join(permissions),
                },
            )
            self.db.commit()
            return cursor.rowcount != 0

    async def get_all_users(self):
        with closing(self.db.cursor()) as cursor:
            cursor.execute("SELECT username, permissions FROM users")
            return [map_user(r) for r in cursor.fetchall()]

    async def new_user(self, username, permissions, token):
        with closing(self.db.cursor()) as cursor:
            try:
                cursor.execute(
                    """
                    INSERT INTO users (username, token, permissions) VALUES (:username, :token, :permissions)
                    """,
                    {
                        "username": username,
                        "token": token,
                        "permissions": " ".join(permissions),
                    },
                )
                self.db.commit()
                return True
            except sqlite3.IntegrityError:
                return False

    async def delete_user(self, username):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                """
                DELETE FROM users WHERE username=:username
                """,
                {
                    "username": username,
                },
            )
            self.db.commit()
            return cursor.rowcount != 0

    async def get_usage(self):
        usage = {}
        with closing(self.db.cursor()) as cursor:
            cursor.execute(
                f"""
                SELECT name FROM {_schema_table_name(self.sqlite_version)} WHERE type = 'table' AND name NOT LIKE 'sqlite_%'
                """
            )
            for row in cursor.fetchall():
                cursor.execute(
                    """
                    SELECT COUNT() FROM %s
                    """
                    % row["name"],
                )
                usage[row["name"]] = {
                    "rows": cursor.fetchone()[0],
                }
        return usage

    async def get_query_columns(self):
        columns = set()
        for name, typ, _ in UNIHASH_TABLE_DEFINITION + OUTHASH_TABLE_DEFINITION:
            if typ.startswith("TEXT"):
                columns.add(name)
        return list(columns)
