"""BitBake Persistent Data Store

Used to store data in a central location such that other threads/tasks can
access them at some future date.  Acts as a convenience wrapper around sqlite,
currently, providing a key/value store accessed by 'domain'.
"""

# Copyright (C) 2007        Richard Purdie
# Copyright (C) 2010        Chris Larson <chris_larson@mentor.com>
#
# SPDX-License-Identifier: GPL-2.0-only
#

import collections
import collections.abc
import contextlib
import functools
import logging
import os.path
import sqlite3
import sys
from collections.abc import Mapping

sqlversion = sqlite3.sqlite_version_info
if sqlversion[0] < 3 or (sqlversion[0] == 3 and sqlversion[1] < 3):
    raise Exception("sqlite3 version 3.3.0 or later is required.")


logger = logging.getLogger("BitBake.PersistData")

@functools.total_ordering
class SQLTable(collections.abc.MutableMapping):
    class _Decorators(object):
        @staticmethod
        def retry(*, reconnect=True):
            """
            Decorator that restarts a function if a database locked sqlite
            exception occurs. If reconnect is True, the database connection
            will be closed and reopened each time a failure occurs
            """
            def retry_wrapper(f):
                def wrap_func(self, *args, **kwargs):
                    # Reconnect if necessary
                    if self.connection is None and reconnect:
                        self.reconnect()

                    count = 0
                    while True:
                        try:
                            return f(self, *args, **kwargs)
                        except sqlite3.OperationalError as exc:
                            if count < 500 and ('is locked' in str(exc) or 'locking protocol' in str(exc)):
                                count = count + 1
                                if reconnect:
                                    self.reconnect()
                                continue
                            raise
                return wrap_func
            return retry_wrapper

        @staticmethod
        def transaction(f):
            """
            Decorator that starts a database transaction and creates a database
            cursor for performing queries. If no exception is thrown, the
            database results are committed. If an exception occurs, the database
            is rolled back. In all cases, the cursor is closed after the
            function ends.

            Note that the cursor is passed as an extra argument to the function
            after `self` and before any of the normal arguments
            """
            def wrap_func(self, *args, **kwargs):
                # Context manager will COMMIT the database on success,
                # or ROLLBACK on an exception
                with self.connection:
                    # Automatically close the cursor when done
                    with contextlib.closing(self.connection.cursor()) as cursor:
                        return f(self, cursor, *args, **kwargs)
            return wrap_func

    """Object representing a table/domain in the database"""
    def __init__(self, cachefile, table):
        self.cachefile = cachefile
        self.table = table

        self.connection = None
        self._execute_single("CREATE TABLE IF NOT EXISTS %s(key TEXT PRIMARY KEY NOT NULL, value TEXT);" % table)

    @_Decorators.retry(reconnect=False)
    @_Decorators.transaction
    def _setup_database(self, cursor):
        cursor.execute("pragma synchronous = off;")
        # Enable WAL and keep the autocheckpoint length small (the default is
        # usually 1000). Persistent caches are usually read-mostly, so keeping
        # this short will keep readers running quickly
        cursor.execute("pragma journal_mode = WAL;")
        cursor.execute("pragma wal_autocheckpoint = 100;")

    def reconnect(self):
        if self.connection is not None:
            self.connection.close()
        self.connection = sqlite3.connect(self.cachefile, timeout=5)
        self.connection.text_factory = str
        self._setup_database()

    @_Decorators.retry()
    @_Decorators.transaction
    def _execute_single(self, cursor, *query):
        """
        Executes a single query and discards the results. This correctly closes
        the database cursor when finished
        """
        cursor.execute(*query)

    @_Decorators.retry()
    def _row_iter(self, f, *query):
        """
        Helper function that returns a row iterator. Each time __next__ is
        called on the iterator, the provided function is evaluated to determine
        the return value
        """
        class CursorIter(object):
            def __init__(self, cursor):
                self.cursor = cursor

            def __iter__(self):
                return self

            def __next__(self):
                row = self.cursor.fetchone()
                if row is None:
                    self.cursor.close()
                    raise StopIteration
                return f(row)

            def __enter__(self):
                return self

            def __exit__(self, typ, value, traceback):
                self.cursor.close()
                return False

        cursor = self.connection.cursor()
        try:
            cursor.execute(*query)
            return CursorIter(cursor)
        except:
            cursor.close()

    def __enter__(self):
        self.connection.__enter__()
        return self

    def __exit__(self, *excinfo):
        self.connection.__exit__(*excinfo)
        self.connection.close()

    @_Decorators.retry()
    @_Decorators.transaction
    def __getitem__(self, cursor, key):
        cursor.execute("SELECT * from %s where key=?;" % self.table, [key])
        row = cursor.fetchone()
        if row is not None:
            return row[1]
        raise KeyError(key)

    @_Decorators.retry()
    @_Decorators.transaction
    def __delitem__(self, cursor, key):
        if key not in self:
            raise KeyError(key)
        cursor.execute("DELETE from %s where key=?;" % self.table, [key])

    @_Decorators.retry()
    @_Decorators.transaction
    def __setitem__(self, cursor, key, value):
        if not isinstance(key, str):
            raise TypeError('Only string keys are supported')
        elif not isinstance(value, str):
            raise TypeError('Only string values are supported')

        # Ensure the entire transaction (including SELECT) executes under write lock
        cursor.execute("BEGIN EXCLUSIVE")

        cursor.execute("SELECT * from %s where key=?;" % self.table, [key])
        row = cursor.fetchone()
        if row is not None:
            cursor.execute("UPDATE %s SET value=? WHERE key=?;" % self.table, [value, key])
        else:
            cursor.execute("INSERT into %s(key, value) values (?, ?);" % self.table, [key, value])

    @_Decorators.retry()
    @_Decorators.transaction
    def __contains__(self, cursor, key):
        cursor.execute('SELECT * from %s where key=?;' % self.table, [key])
        return cursor.fetchone() is not None

    @_Decorators.retry()
    @_Decorators.transaction
    def __len__(self, cursor):
        cursor.execute("SELECT COUNT(key) FROM %s;" % self.table)
        row = cursor.fetchone()
        if row is not None:
            return row[0]

    def __iter__(self):
        return self._row_iter(lambda row: row[0], "SELECT key from %s;" % self.table)

    def __lt__(self, other):
        if not isinstance(other, Mapping):
            raise NotImplementedError()

        return len(self) < len(other)

    def get_by_pattern(self, pattern):
        return self._row_iter(lambda row: row[1], "SELECT * FROM %s WHERE key LIKE ?;" %
                              self.table, [pattern])

    def values(self):
        return list(self.itervalues())

    def itervalues(self):
        return self._row_iter(lambda row: row[0], "SELECT value FROM %s;" %
                              self.table)

    def items(self):
        return list(self.iteritems())

    def iteritems(self):
        return self._row_iter(lambda row: (row[0], row[1]), "SELECT * FROM %s;" %
                              self.table)

    @_Decorators.retry()
    @_Decorators.transaction
    def clear(self, cursor):
        cursor.execute("DELETE FROM %s;" % self.table)

    def has_key(self, key):
        return key in self

def persist(domain, d):
    """Convenience factory for SQLTable objects based upon metadata"""
    import bb.utils
    cachedir = (d.getVar("PERSISTENT_DIR") or
                d.getVar("CACHE"))
    if not cachedir:
        logger.critical("Please set the 'PERSISTENT_DIR' or 'CACHE' variable")
        sys.exit(1)

    bb.utils.mkdirhier(cachedir)
    cachefile = os.path.join(cachedir, "bb_persist_data.sqlite3")

    try:
        return SQLTable(cachefile, domain)
    except sqlite3.OperationalError:
        # Sqlite fails to open database when its path is too long.
        # After testing, 504 is the biggest path length that can be opened by
        # sqlite.
        # Note: This code is called before sanity.bbclass and its path length
        # check
        max_len = 504
        if len(cachefile) > max_len:
            logger.critical("The path of the cache file is too long "
                    "({0} chars > {1}) to be opened by sqlite! "
                    "Your cache file is \"{2}\"".format(
                        len(cachefile),
                        max_len,
                        cachefile))
            sys.exit(1)
        else:
            raise
