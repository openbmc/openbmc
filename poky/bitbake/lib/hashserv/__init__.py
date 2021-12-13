# Copyright (C) 2018-2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import asyncio
from contextlib import closing
import re
import sqlite3
import itertools
import json

UNIX_PREFIX = "unix://"

ADDR_TYPE_UNIX = 0
ADDR_TYPE_TCP = 1

# The Python async server defaults to a 64K receive buffer, so we hardcode our
# maximum chunk size. It would be better if the client and server reported to
# each other what the maximum chunk sizes were, but that will slow down the
# connection setup with a round trip delay so I'd rather not do that unless it
# is necessary
DEFAULT_MAX_CHUNK = 32 * 1024

UNIHASH_TABLE_DEFINITION = (
    ("method", "TEXT NOT NULL", "UNIQUE"),
    ("taskhash", "TEXT NOT NULL", "UNIQUE"),
    ("unihash", "TEXT NOT NULL", ""),
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

def _make_table(cursor, name, definition):
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS {name} (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            {fields}
            UNIQUE({unique})
            )
        '''.format(
            name=name,
            fields=" ".join("%s %s," % (name, typ) for name, typ, _ in definition),
            unique=", ".join(name for name, _, flags in definition if "UNIQUE" in flags)
    ))


def setup_database(database, sync=True):
    db = sqlite3.connect(database)
    db.row_factory = sqlite3.Row

    with closing(db.cursor()) as cursor:
        _make_table(cursor, "unihashes_v2", UNIHASH_TABLE_DEFINITION)
        _make_table(cursor, "outhashes_v2", OUTHASH_TABLE_DEFINITION)

        cursor.execute('PRAGMA journal_mode = WAL')
        cursor.execute('PRAGMA synchronous = %s' % ('NORMAL' if sync else 'OFF'))

        # Drop old indexes
        cursor.execute('DROP INDEX IF EXISTS taskhash_lookup')
        cursor.execute('DROP INDEX IF EXISTS outhash_lookup')
        cursor.execute('DROP INDEX IF EXISTS taskhash_lookup_v2')
        cursor.execute('DROP INDEX IF EXISTS outhash_lookup_v2')

        # TODO: Upgrade from tasks_v2?
        cursor.execute('DROP TABLE IF EXISTS tasks_v2')

        # Create new indexes
        cursor.execute('CREATE INDEX IF NOT EXISTS taskhash_lookup_v3 ON unihashes_v2 (method, taskhash)')
        cursor.execute('CREATE INDEX IF NOT EXISTS outhash_lookup_v3 ON outhashes_v2 (method, outhash)')

    return db


def parse_address(addr):
    if addr.startswith(UNIX_PREFIX):
        return (ADDR_TYPE_UNIX, (addr[len(UNIX_PREFIX):],))
    else:
        m = re.match(r'\[(?P<host>[^\]]*)\]:(?P<port>\d+)$', addr)
        if m is not None:
            host = m.group('host')
            port = m.group('port')
        else:
            host, port = addr.split(':')

        return (ADDR_TYPE_TCP, (host, int(port)))


def chunkify(msg, max_chunk):
    if len(msg) < max_chunk - 1:
        yield ''.join((msg, "\n"))
    else:
        yield ''.join((json.dumps({
                'chunk-stream': None
            }), "\n"))

        args = [iter(msg)] * (max_chunk - 1)
        for m in map(''.join, itertools.zip_longest(*args, fillvalue='')):
            yield ''.join(itertools.chain(m, "\n"))
        yield "\n"


def create_server(addr, dbname, *, sync=True, upstream=None, read_only=False):
    from . import server
    db = setup_database(dbname, sync=sync)
    s = server.Server(db, upstream=upstream, read_only=read_only)

    (typ, a) = parse_address(addr)
    if typ == ADDR_TYPE_UNIX:
        s.start_unix_server(*a)
    else:
        s.start_tcp_server(*a)

    return s


def create_client(addr):
    from . import client
    c = client.Client()

    (typ, a) = parse_address(addr)
    if typ == ADDR_TYPE_UNIX:
        c.connect_unix(*a)
    else:
        c.connect_tcp(*a)

    return c

async def create_async_client(addr):
    from . import client
    c = client.AsyncClient()

    (typ, a) = parse_address(addr)
    if typ == ADDR_TYPE_UNIX:
        await c.connect_unix(*a)
    else:
        await c.connect_tcp(*a)

    return c
