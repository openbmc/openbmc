"""BitBake Persistent Data Store

Used to store data in a central location such that other threads/tasks can
access them at some future date.  Acts as a convenience wrapper around sqlite,
currently, providing a key/value store accessed by 'domain'.
"""

# Copyright (C) 2007        Richard Purdie
# Copyright (C) 2010        Chris Larson <chris_larson@mentor.com>
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import collections
import logging
import os.path
import sys
import warnings
from bb.compat import total_ordering
from collections import Mapping
import sqlite3

sqlversion = sqlite3.sqlite_version_info
if sqlversion[0] < 3 or (sqlversion[0] == 3 and sqlversion[1] < 3):
    raise Exception("sqlite3 version 3.3.0 or later is required.")


logger = logging.getLogger("BitBake.PersistData")
if hasattr(sqlite3, 'enable_shared_cache'):
    try:
        sqlite3.enable_shared_cache(True)
    except sqlite3.OperationalError:
        pass


@total_ordering
class SQLTable(collections.MutableMapping):
    """Object representing a table/domain in the database"""
    def __init__(self, cachefile, table):
        self.cachefile = cachefile
        self.table = table
        self.cursor = connect(self.cachefile)

        self._execute("CREATE TABLE IF NOT EXISTS %s(key TEXT, value TEXT);"
                      % table)

    def _execute(self, *query):
        """Execute a query, waiting to acquire a lock if necessary"""
        count = 0
        while True:
            try:
                return self.cursor.execute(*query)
            except sqlite3.OperationalError as exc:
                if 'database is locked' in str(exc) and count < 500:
                    count = count + 1
                    self.cursor.close()
                    self.cursor = connect(self.cachefile)
                    continue
                raise

    def __enter__(self):
        self.cursor.__enter__()
        return self

    def __exit__(self, *excinfo):
        self.cursor.__exit__(*excinfo)

    def __getitem__(self, key):
        data = self._execute("SELECT * from %s where key=?;" %
                             self.table, [key])
        for row in data:
            return row[1]
        raise KeyError(key)

    def __delitem__(self, key):
        if key not in self:
            raise KeyError(key)
        self._execute("DELETE from %s where key=?;" % self.table, [key])

    def __setitem__(self, key, value):
        if not isinstance(key, str):
            raise TypeError('Only string keys are supported')
        elif not isinstance(value, str):
            raise TypeError('Only string values are supported')

        data = self._execute("SELECT * from %s where key=?;" %
                                   self.table, [key])
        exists = len(list(data))
        if exists:
            self._execute("UPDATE %s SET value=? WHERE key=?;" % self.table,
                          [value, key])
        else:
            self._execute("INSERT into %s(key, value) values (?, ?);" %
                          self.table, [key, value])

    def __contains__(self, key):
        return key in set(self)

    def __len__(self):
        data = self._execute("SELECT COUNT(key) FROM %s;" % self.table)
        for row in data:
            return row[0]

    def __iter__(self):
        data = self._execute("SELECT key FROM %s;" % self.table)
        return (row[0] for row in data)

    def __lt__(self, other):
        if not isinstance(other, Mapping):
            raise NotImplemented

        return len(self) < len(other)

    def get_by_pattern(self, pattern):
        data = self._execute("SELECT * FROM %s WHERE key LIKE ?;" %
                             self.table, [pattern])
        return [row[1] for row in data]

    def values(self):
        return list(self.itervalues())

    def itervalues(self):
        data = self._execute("SELECT value FROM %s;" % self.table)
        return (row[0] for row in data)

    def items(self):
        return list(self.iteritems())

    def iteritems(self):
        return self._execute("SELECT * FROM %s;" % self.table)

    def clear(self):
        self._execute("DELETE FROM %s;" % self.table)

    def has_key(self, key):
        return key in self


class PersistData(object):
    """Deprecated representation of the bitbake persistent data store"""
    def __init__(self, d):
        warnings.warn("Use of PersistData is deprecated.  Please use "
                      "persist(domain, d) instead.",
                      category=DeprecationWarning,
                      stacklevel=2)

        self.data = persist(d)
        logger.debug(1, "Using '%s' as the persistent data cache",
                     self.data.filename)

    def addDomain(self, domain):
        """
        Add a domain (pending deprecation)
        """
        return self.data[domain]

    def delDomain(self, domain):
        """
        Removes a domain and all the data it contains
        """
        del self.data[domain]

    def getKeyValues(self, domain):
        """
        Return a list of key + value pairs for a domain
        """
        return list(self.data[domain].items())

    def getValue(self, domain, key):
        """
        Return the value of a key for a domain
        """
        return self.data[domain][key]

    def setValue(self, domain, key, value):
        """
        Sets the value of a key for a domain
        """
        self.data[domain][key] = value

    def delValue(self, domain, key):
        """
        Deletes a key/value pair
        """
        del self.data[domain][key]

def connect(database):
    connection = sqlite3.connect(database, timeout=5, isolation_level=None)
    connection.execute("pragma synchronous = off;")
    connection.text_factory = str
    return connection

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
    return SQLTable(cachefile, domain)
