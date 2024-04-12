#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os.path
import errno
import prserv
import time

try:
    import sqlite3
except ImportError:
    from pysqlite2 import dbapi2 as sqlite3

logger = logging.getLogger("BitBake.PRserv")

sqlversion = sqlite3.sqlite_version_info
if sqlversion[0] < 3 or (sqlversion[0] == 3 and sqlversion[1] < 3):
    raise Exception("sqlite3 version 3.3.0 or later is required.")

#
# "No History" mode - for a given query tuple (version, pkgarch, checksum),
# the returned value will be the largest among all the values of the same
# (version, pkgarch). This means the PR value returned can NOT be decremented.
#
# "History" mode - Return a new higher value for previously unseen query
# tuple (version, pkgarch, checksum), otherwise return historical value.
# Value can decrement if returning to a previous build.
#

class PRTable(object):
    def __init__(self, conn, table, nohist, read_only):
        self.conn = conn
        self.nohist = nohist
        self.read_only = read_only
        self.dirty = False
        if nohist:
            self.table = "%s_nohist" % table
        else:
            self.table = "%s_hist" % table

        if self.read_only:
            table_exists = self._execute(
                        "SELECT count(*) FROM sqlite_master \
                        WHERE type='table' AND name='%s'" % (self.table))
            if not table_exists:
                raise prserv.NotFoundError
        else:
            self._execute("CREATE TABLE IF NOT EXISTS %s \
                        (version TEXT NOT NULL, \
                        pkgarch TEXT NOT NULL,  \
                        checksum TEXT NOT NULL, \
                        value INTEGER, \
                        PRIMARY KEY (version, pkgarch, checksum));" % self.table)

    def _execute(self, *query):
        """Execute a query, waiting to acquire a lock if necessary"""
        start = time.time()
        end = start + 20
        while True:
            try:
                return self.conn.execute(*query)
            except sqlite3.OperationalError as exc:
                if "is locked" in str(exc) and end > time.time():
                    continue
                raise exc

    def sync(self):
        if not self.read_only:
            self.conn.commit()
            self._execute("BEGIN EXCLUSIVE TRANSACTION")

    def sync_if_dirty(self):
        if self.dirty:
            self.sync()
            self.dirty = False

    def test_package(self, version, pkgarch):
        """Returns whether the specified package version is found in the database for the specified architecture"""

        # Just returns the value if found or None otherwise
        data=self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=?;" % self.table,
                           (version, pkgarch))
        row=data.fetchone()
        if row is not None:
            return True
        else:
            return False

    def test_value(self, version, pkgarch, value):
        """Returns whether the specified value is found in the database for the specified package and architecture"""

        # Just returns the value if found or None otherwise
        data=self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? and value=?;" % self.table,
                           (version, pkgarch, value))
        row=data.fetchone()
        if row is not None:
            return True
        else:
            return False

    def find_value(self, version, pkgarch, checksum):
        """Returns the value for the specified checksum if found or None otherwise."""

        data=self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=?;" % self.table,
                           (version, pkgarch, checksum))
        row=data.fetchone()
        if row is not None:
            return row[0]
        else:
            return None

    def find_max_value(self, version, pkgarch):
        """Returns the greatest value for (version, pkgarch), or None if not found. Doesn't create a new value"""

        data = self._execute("SELECT max(value) FROM %s where version=? AND pkgarch=?;" % (self.table),
                             (version, pkgarch))
        row = data.fetchone()
        if row is not None:
            return row[0]
        else:
            return None

    def _get_value_hist(self, version, pkgarch, checksum):
        data=self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=?;" % self.table,
                           (version, pkgarch, checksum))
        row=data.fetchone()
        if row is not None:
            return row[0]
        else:
            #no value found, try to insert
            if self.read_only:
                data = self._execute("SELECT ifnull(max(value)+1, 0) FROM %s where version=? AND pkgarch=?;" % (self.table),
                                   (version, pkgarch))
                row = data.fetchone()
                if row is not None:
                    return row[0]
                else:
                    return 0

            try:
                self._execute("INSERT INTO %s VALUES (?, ?, ?, (select ifnull(max(value)+1, 0) from %s where version=? AND pkgarch=?));"
                           % (self.table, self.table),
                           (version, pkgarch, checksum, version, pkgarch))
            except sqlite3.IntegrityError as exc:
                logger.error(str(exc))

            self.dirty = True

            data=self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=?;" % self.table,
                               (version, pkgarch, checksum))
            row=data.fetchone()
            if row is not None:
                return row[0]
            else:
                raise prserv.NotFoundError

    def _get_value_no_hist(self, version, pkgarch, checksum):
        data=self._execute("SELECT value FROM %s \
                            WHERE version=? AND pkgarch=? AND checksum=? AND \
                            value >= (select max(value) from %s where version=? AND pkgarch=?);"
                            % (self.table, self.table),
                            (version, pkgarch, checksum, version, pkgarch))
        row=data.fetchone()
        if row is not None:
            return row[0]
        else:
            #no value found, try to insert
            if self.read_only:
                data = self._execute("SELECT ifnull(max(value)+1, 0) FROM %s where version=? AND pkgarch=?;" % (self.table),
                                   (version, pkgarch))
                return data.fetchone()[0]

            try:
                self._execute("INSERT OR REPLACE INTO %s VALUES (?, ?, ?, (select ifnull(max(value)+1, 0) from %s where version=? AND pkgarch=?));"
                               % (self.table, self.table),
                               (version, pkgarch, checksum, version, pkgarch))
            except sqlite3.IntegrityError as exc:
                logger.error(str(exc))
                self.conn.rollback()

            self.dirty = True

            data=self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=?;" % self.table,
                               (version, pkgarch, checksum))
            row=data.fetchone()
            if row is not None:
                return row[0]
            else:
                raise prserv.NotFoundError

    def get_value(self, version, pkgarch, checksum):
        if self.nohist:
            return self._get_value_no_hist(version, pkgarch, checksum)
        else:
            return self._get_value_hist(version, pkgarch, checksum)

    def _import_hist(self, version, pkgarch, checksum, value):
        if self.read_only:
            return None

        val = None
        data = self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=?;" % self.table,
                           (version, pkgarch, checksum))
        row = data.fetchone()
        if row is not None:
            val=row[0]
        else:
            #no value found, try to insert
            try:
                self._execute("INSERT INTO %s VALUES (?, ?, ?, ?);"  % (self.table),
                           (version, pkgarch, checksum, value))
            except sqlite3.IntegrityError as exc:
                logger.error(str(exc))

            self.dirty = True

            data = self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=?;" % self.table,
                           (version, pkgarch, checksum))
            row = data.fetchone()
            if row is not None:
                val = row[0]
        return val

    def _import_no_hist(self, version, pkgarch, checksum, value):
        if self.read_only:
            return None

        try:
            #try to insert
            self._execute("INSERT INTO %s VALUES (?, ?, ?, ?);"  % (self.table),
                           (version, pkgarch, checksum, value))
        except sqlite3.IntegrityError as exc:
            #already have the record, try to update
            try:
                self._execute("UPDATE %s SET value=? WHERE version=? AND pkgarch=? AND checksum=? AND value<?"
                              % (self.table),
                               (value, version, pkgarch, checksum, value))
            except sqlite3.IntegrityError as exc:
                logger.error(str(exc))

        self.dirty = True

        data = self._execute("SELECT value FROM %s WHERE version=? AND pkgarch=? AND checksum=? AND value>=?;" % self.table,
                            (version, pkgarch, checksum, value))
        row=data.fetchone()
        if row is not None:
            return row[0]
        else:
            return None

    def importone(self, version, pkgarch, checksum, value):
        if self.nohist:
            return self._import_no_hist(version, pkgarch, checksum, value)
        else:
            return self._import_hist(version, pkgarch, checksum, value)

    def export(self, version, pkgarch, checksum, colinfo):
        metainfo = {}
        #column info
        if colinfo:
            metainfo["tbl_name"] = self.table
            metainfo["core_ver"] = prserv.__version__
            metainfo["col_info"] = []
            data = self._execute("PRAGMA table_info(%s);" % self.table)
            for row in data:
                col = {}
                col["name"] = row["name"]
                col["type"] = row["type"]
                col["notnull"] = row["notnull"]
                col["dflt_value"] = row["dflt_value"]
                col["pk"] = row["pk"]
                metainfo["col_info"].append(col)

        #data info
        datainfo = []

        if self.nohist:
            sqlstmt = "SELECT T1.version, T1.pkgarch, T1.checksum, T1.value FROM %s as T1, \
                    (SELECT version, pkgarch, max(value) as maxvalue FROM %s GROUP BY version, pkgarch) as T2 \
                    WHERE T1.version=T2.version AND T1.pkgarch=T2.pkgarch AND T1.value=T2.maxvalue " % (self.table, self.table)
        else:
            sqlstmt = "SELECT * FROM %s as T1 WHERE 1=1 " % self.table
        sqlarg = []
        where = ""
        if version:
            where += "AND T1.version=? "
            sqlarg.append(str(version))
        if pkgarch:
            where += "AND T1.pkgarch=? "
            sqlarg.append(str(pkgarch))
        if checksum:
            where += "AND T1.checksum=? "
            sqlarg.append(str(checksum))

        sqlstmt += where + ";"

        if len(sqlarg):
            data = self._execute(sqlstmt, tuple(sqlarg))
        else:
            data = self._execute(sqlstmt)
        for row in data:
            if row["version"]:
                col = {}
                col["version"] = row["version"]
                col["pkgarch"] = row["pkgarch"]
                col["checksum"] = row["checksum"]
                col["value"] = row["value"]
                datainfo.append(col)
        return (metainfo, datainfo)

    def dump_db(self, fd):
        writeCount = 0
        for line in self.conn.iterdump():
            writeCount = writeCount + len(line) + 1
            fd.write(line)
            fd.write("\n")
        return writeCount

class PRData(object):
    """Object representing the PR database"""
    def __init__(self, filename, nohist=True, read_only=False):
        self.filename=os.path.abspath(filename)
        self.nohist=nohist
        self.read_only = read_only
        #build directory hierarchy
        try:
            os.makedirs(os.path.dirname(self.filename))
        except OSError as e:
            if e.errno != errno.EEXIST:
                raise e
        uri = "file:%s%s" % (self.filename, "?mode=ro" if self.read_only else "")
        logger.debug("Opening PRServ database '%s'" % (uri))
        self.connection=sqlite3.connect(uri, uri=True, isolation_level="EXCLUSIVE", check_same_thread = False)
        self.connection.row_factory=sqlite3.Row
        if not self.read_only:
            self.connection.execute("pragma synchronous = off;")
            self.connection.execute("PRAGMA journal_mode = MEMORY;")
        self._tables={}

    def disconnect(self):
        self.connection.close()

    def __getitem__(self, tblname):
        if not isinstance(tblname, str):
            raise TypeError("tblname argument must be a string, not '%s'" %
                            type(tblname))
        if tblname in self._tables:
            return self._tables[tblname]
        else:
            tableobj = self._tables[tblname] = PRTable(self.connection, tblname, self.nohist, self.read_only)
            return tableobj

    def __delitem__(self, tblname):
        if tblname in self._tables:
            del self._tables[tblname]
        logger.info("drop table %s" % (tblname))
        self.connection.execute("DROP TABLE IF EXISTS %s;" % tblname)
