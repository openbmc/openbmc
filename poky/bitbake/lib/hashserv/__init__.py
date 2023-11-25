# Copyright (C) 2018-2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import asyncio
from contextlib import closing
import re
import itertools
import json
from collections import namedtuple
from urllib.parse import urlparse

UNIX_PREFIX = "unix://"
WS_PREFIX = "ws://"
WSS_PREFIX = "wss://"

ADDR_TYPE_UNIX = 0
ADDR_TYPE_TCP = 1
ADDR_TYPE_WS = 2

User = namedtuple("User", ("username", "permissions"))


def parse_address(addr):
    if addr.startswith(UNIX_PREFIX):
        return (ADDR_TYPE_UNIX, (addr[len(UNIX_PREFIX) :],))
    elif addr.startswith(WS_PREFIX) or addr.startswith(WSS_PREFIX):
        return (ADDR_TYPE_WS, (addr,))
    else:
        m = re.match(r"\[(?P<host>[^\]]*)\]:(?P<port>\d+)$", addr)
        if m is not None:
            host = m.group("host")
            port = m.group("port")
        else:
            host, port = addr.split(":")

        return (ADDR_TYPE_TCP, (host, int(port)))


def create_server(
    addr,
    dbname,
    *,
    sync=True,
    upstream=None,
    read_only=False,
    db_username=None,
    db_password=None,
    anon_perms=None,
    admin_username=None,
    admin_password=None,
):
    def sqlite_engine():
        from .sqlite import DatabaseEngine

        return DatabaseEngine(dbname, sync)

    def sqlalchemy_engine():
        from .sqlalchemy import DatabaseEngine

        return DatabaseEngine(dbname, db_username, db_password)

    from . import server

    if "://" in dbname:
        db_engine = sqlalchemy_engine()
    else:
        db_engine = sqlite_engine()

    if anon_perms is None:
        anon_perms = server.DEFAULT_ANON_PERMS

    s = server.Server(
        db_engine,
        upstream=upstream,
        read_only=read_only,
        anon_perms=anon_perms,
        admin_username=admin_username,
        admin_password=admin_password,
    )

    (typ, a) = parse_address(addr)
    if typ == ADDR_TYPE_UNIX:
        s.start_unix_server(*a)
    elif typ == ADDR_TYPE_WS:
        url = urlparse(a[0])
        s.start_websocket_server(url.hostname, url.port)
    else:
        s.start_tcp_server(*a)

    return s


def create_client(addr, username=None, password=None):
    from . import client

    c = client.Client(username, password)

    try:
        (typ, a) = parse_address(addr)
        if typ == ADDR_TYPE_UNIX:
            c.connect_unix(*a)
        elif typ == ADDR_TYPE_WS:
            c.connect_websocket(*a)
        else:
            c.connect_tcp(*a)
        return c
    except Exception as e:
        c.close()
        raise e


async def create_async_client(addr, username=None, password=None):
    from . import client

    c = client.AsyncClient(username, password)

    try:
        (typ, a) = parse_address(addr)
        if typ == ADDR_TYPE_UNIX:
            await c.connect_unix(*a)
        elif typ == ADDR_TYPE_WS:
            await c.connect_websocket(*a)
        else:
            await c.connect_tcp(*a)

        return c
    except Exception as e:
        await c.close()
        raise e
