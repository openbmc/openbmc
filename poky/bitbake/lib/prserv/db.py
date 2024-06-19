#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import os.path
import errno
import prserv
import sqlite3

from contextlib import closing
from . import increase_revision, revision_greater, revision_smaller

logger = logging.getLogger("BitBake.PRserv")

#
# "No History" mode - for a given query tuple (version, pkgarch, checksum),
# the returned value will be the largest among all the values of the same
# (version, pkgarch). This means the PR value returned can NOT be decremented.
#
# "History" mode - Return a new higher value for previously unseen query
# tuple (version, pkgarch, checksum), otherwise return historical value.
# Value can decrement if returning to a previous build.

class PRTable(object):
    def __init__(self, conn, table, read_only):
        self.conn = conn
        self.read_only = read_only
        self.table = table

        # Creating the table even if the server is read-only.
        # This avoids a race condition if a shared database
        # is accessed by a read-only server first.

        with closing(self.conn.cursor()) as cursor:
            cursor.execute("CREATE TABLE IF NOT EXISTS %s \
                        (version TEXT NOT NULL, \
                        pkgarch TEXT NOT NULL,  \
                        checksum TEXT NOT NULL, \
                        value TEXT, \
                        PRIMARY KEY (version, pkgarch, checksum, value));" % self.table)
            self.conn.commit()

    def _extremum_value(self, rows, is_max):
        value = None

        for row in rows:
            current_value = row[0]
            if value is None:
                value = current_value
            else:
                if is_max:
                    is_new_extremum = revision_greater(current_value, value)
                else:
                    is_new_extremum = revision_smaller(current_value, value)
                if  is_new_extremum:
                    value = current_value
        return value

    def _max_value(self, rows):
        return self._extremum_value(rows, True)

    def _min_value(self, rows):
        return self._extremum_value(rows, False)

    def test_package(self, version, pkgarch):
        """Returns whether the specified package version is found in the database for the specified architecture"""

        # Just returns the value if found or None otherwise
        with closing(self.conn.cursor()) as cursor:
            data=cursor.execute("SELECT value FROM %s WHERE version=? AND pkgarch=?;" % self.table,
                               (version, pkgarch))
            row=data.fetchone()
            if row is not None:
                return True
            else:
                return False

    def test_checksum_value(self, version, pkgarch, checksum, value):
        """Returns whether the specified value is found in the database for the specified package, architecture and checksum"""

        with closing(self.conn.cursor()) as cursor:
            data=cursor.execute("SELECT value FROM %s WHERE version=? AND pkgarch=? and checksum=? and value=?;" % self.table,
                               (version, pkgarch, checksum, value))
            row=data.fetchone()
            if row is not None:
                return True
            else:
                return False

    def test_value(self, version, pkgarch, value):
        """Returns whether the specified value is found in the database for the specified package and architecture"""

        # Just returns the value if found or None otherwise
        with closing(self.conn.cursor()) as cursor:
            data=cursor.execute("SELECT value FROM %s WHERE version=? AND pkgarch=? and value=?;" % self.table,
                               (version, pkgarch, value))
            row=data.fetchone()
            if row is not None:
                return True
            else:
                return False


    def find_package_max_value(self, version, pkgarch):
        """Returns the greatest value for (version, pkgarch), or None if not found. Doesn't create a new value"""

        with closing(self.conn.cursor()) as cursor:
            data = cursor.execute("SELECT value FROM %s where version=? AND pkgarch=?;" % (self.table),
                                 (version, pkgarch))
            rows = data.fetchall()
            value = self._max_value(rows)
            return value

    def find_value(self, version, pkgarch, checksum, history=False):
        """Returns the value for the specified checksum if found or None otherwise."""

        if history:
            return self.find_min_value(version, pkgarch, checksum)
        else:
            return self.find_max_value(version, pkgarch, checksum)


    def _find_extremum_value(self, version, pkgarch, checksum, is_max):
        """Returns the maximum (if is_max is True) or minimum (if is_max is False) value
           for (version, pkgarch, checksum), or None if not found. Doesn't create a new value"""

        with closing(self.conn.cursor()) as cursor:
            data = cursor.execute("SELECT value FROM %s where version=? AND pkgarch=? AND checksum=?;" % (self.table),
                                 (version, pkgarch, checksum))
            rows = data.fetchall()
            return self._extremum_value(rows, is_max)

    def find_max_value(self, version, pkgarch, checksum):
        return self._find_extremum_value(version, pkgarch, checksum, True)

    def find_min_value(self, version, pkgarch, checksum):
        return self._find_extremum_value(version, pkgarch, checksum, False)

    def find_new_subvalue(self, version, pkgarch, base):
        """Take and increase the greatest "<base>.y" value for (version, pkgarch), or return "<base>.0" if not found.
        This doesn't store a new value."""

        with closing(self.conn.cursor()) as cursor:
            data = cursor.execute("SELECT value FROM %s where version=? AND pkgarch=? AND value LIKE '%s.%%';" % (self.table, base),
                                 (version, pkgarch))
            rows = data.fetchall()
            value = self._max_value(rows)

            if value is not None:
                return increase_revision(value)
            else:
                return base + ".0"

    def store_value(self, version, pkgarch, checksum, value):
        """Store value in the database"""

        if not self.read_only and not self.test_checksum_value(version, pkgarch, checksum, value):
            with closing(self.conn.cursor()) as cursor:
                cursor.execute("INSERT INTO %s VALUES (?, ?, ?, ?);"  % (self.table),
                           (version, pkgarch, checksum, value))
                self.conn.commit()

    def _get_value(self, version, pkgarch, checksum, history):

        max_value = self.find_package_max_value(version, pkgarch)

        if max_value is None:
            # version, pkgarch completely unknown. Return initial value.
            return "0"

        value = self.find_value(version, pkgarch, checksum, history)

        if value is None:
            # version, pkgarch found but not checksum. Create a new value from the maximum one
            return increase_revision(max_value)

        if history:
            return value

        # "no history" mode - If the value is not the maximum value for the package, need to increase it.
        if max_value > value:
            return increase_revision(max_value)
        else:
            return value

    def get_value(self, version, pkgarch, checksum, history):
        value = self._get_value(version, pkgarch, checksum, history)
        if not self.read_only:
            self.store_value(version, pkgarch, checksum, value)
        return value

    def importone(self, version, pkgarch, checksum, value):
        self.store_value(version, pkgarch, checksum, value)
        return value

    def export(self, version, pkgarch, checksum, colinfo, history=False):
        metainfo = {}
        with closing(self.conn.cursor()) as cursor:
            #column info
            if colinfo:
                metainfo["tbl_name"] = self.table
                metainfo["core_ver"] = prserv.__version__
                metainfo["col_info"] = []
                data = cursor.execute("PRAGMA table_info(%s);" % self.table)
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

            if history:
                sqlstmt = "SELECT * FROM %s as T1 WHERE 1=1 " % self.table
            else:
                sqlstmt = "SELECT T1.version, T1.pkgarch, T1.checksum, T1.value FROM %s as T1, \
                        (SELECT version, pkgarch, max(value) as maxvalue FROM %s GROUP BY version, pkgarch) as T2 \
                        WHERE T1.version=T2.version AND T1.pkgarch=T2.pkgarch AND T1.value=T2.maxvalue " % (self.table, self.table)
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
                data = cursor.execute(sqlstmt, tuple(sqlarg))
            else:
                data = cursor.execute(sqlstmt)
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
    def __init__(self, filename, read_only=False):
        self.filename=os.path.abspath(filename)
        self.read_only = read_only
        #build directory hierarchy
        try:
            os.makedirs(os.path.dirname(self.filename))
        except OSError as e:
            if e.errno != errno.EEXIST:
                raise e
        uri = "file:%s%s" % (self.filename, "?mode=ro" if self.read_only else "")
        logger.debug("Opening PRServ database '%s'" % (uri))
        self.connection=sqlite3.connect(uri, uri=True)
        self.connection.row_factory=sqlite3.Row
        self.connection.execute("PRAGMA synchronous = OFF;")
        self.connection.execute("PRAGMA journal_mode = WAL;")
        self.connection.commit()
        self._tables={}

    def disconnect(self):
        self.connection.commit()
        self.connection.close()

    def __getitem__(self, tblname):
        if not isinstance(tblname, str):
            raise TypeError("tblname argument must be a string, not '%s'" %
                            type(tblname))
        if tblname in self._tables:
            return self._tables[tblname]
        else:
            tableobj = self._tables[tblname] = PRTable(self.connection, tblname, self.read_only)
            return tableobj

    def __delitem__(self, tblname):
        if tblname in self._tables:
            del self._tables[tblname]
        logger.info("drop table %s" % (tblname))
        self.connection.execute("DROP TABLE IF EXISTS %s;" % tblname)
        self.connection.commit()
