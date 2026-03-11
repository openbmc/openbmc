#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import abc
import asyncio
import json
import os
import socket
import sys
import re
import contextlib
from threading import Thread
from .connection import StreamConnection, WebsocketConnection, DEFAULT_MAX_CHUNK
from .exceptions import ConnectionClosedError, InvokeError

UNIX_PREFIX = "unix://"
WS_PREFIX = "ws://"
WSS_PREFIX = "wss://"

ADDR_TYPE_UNIX = 0
ADDR_TYPE_TCP = 1
ADDR_TYPE_WS = 2

WEBSOCKETS_MIN_VERSION = (9, 1)
# Need websockets 10 with python 3.10+
if sys.version_info >= (3, 10, 0):
    WEBSOCKETS_MIN_VERSION = (10, 0)


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


class AsyncClient(object):
    def __init__(
        self,
        proto_name,
        proto_version,
        logger,
        timeout=30,
        server_headers=False,
        headers={},
    ):
        self.socket = None
        self.max_chunk = DEFAULT_MAX_CHUNK
        self.proto_name = proto_name
        self.proto_version = proto_version
        self.logger = logger
        self.timeout = timeout
        self.needs_server_headers = server_headers
        self.server_headers = {}
        self.headers = headers

    async def connect_tcp(self, address, port):
        async def connect_sock():
            reader, writer = await asyncio.open_connection(address, port)
            return StreamConnection(reader, writer, self.timeout, self.max_chunk)

        self._connect_sock = connect_sock

    async def connect_unix(self, path):
        async def connect_sock():
            # AF_UNIX has path length issues so chdir here to workaround
            cwd = os.getcwd()
            try:
                os.chdir(os.path.dirname(path))
                # The socket must be opened synchronously so that CWD doesn't get
                # changed out from underneath us so we pass as a sock into asyncio
                sock = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM, 0)
                sock.connect(os.path.basename(path))
            finally:
                os.chdir(cwd)
            reader, writer = await asyncio.open_unix_connection(sock=sock)
            return StreamConnection(reader, writer, self.timeout, self.max_chunk)

        self._connect_sock = connect_sock

    async def connect_websocket(self, uri):
        import websockets

        try:
            version = tuple(
                int(v)
                for v in websockets.__version__.split(".")[
                    0 : len(WEBSOCKETS_MIN_VERSION)
                ]
            )
        except ValueError:
            raise ImportError(
                f"Unable to parse websockets version '{websockets.__version__}'"
            )

        if version < WEBSOCKETS_MIN_VERSION:
            min_ver_str = ".".join(str(v) for v in WEBSOCKETS_MIN_VERSION)
            raise ImportError(
                f"Websockets version {websockets.__version__} is less than minimum required version {min_ver_str}"
            )

        async def connect_sock():
            try:
                websocket = await websockets.connect(
                    uri,
                    ping_interval=None,
                    open_timeout=self.timeout,
                )
            except asyncio.exceptions.TimeoutError:
                raise ConnectionError("Timeout while connecting to websocket")
            except (OSError, websockets.InvalidHandshake, websockets.InvalidURI) as exc:
                raise ConnectionError(f"Could not connect to websocket: {exc}") from exc
            return WebsocketConnection(websocket, self.timeout)

        self._connect_sock = connect_sock

    async def setup_connection(self):
        # Send headers
        await self.socket.send("%s %s" % (self.proto_name, self.proto_version))
        await self.socket.send(
            "needs-headers: %s" % ("true" if self.needs_server_headers else "false")
        )
        for k, v in self.headers.items():
            await self.socket.send("%s: %s" % (k, v))

        # End of headers
        await self.socket.send("")

        self.server_headers = {}
        if self.needs_server_headers:
            while True:
                line = await self.socket.recv()
                if not line:
                    # End headers
                    break
                tag, value = line.split(":", 1)
                self.server_headers[tag.lower()] = value.strip()

    async def get_header(self, tag, default):
        await self.connect()
        return self.server_headers.get(tag, default)

    async def connect(self):
        if self.socket is None:
            self.socket = await self._connect_sock()
            await self.setup_connection()

    async def disconnect(self):
        if self.socket is not None:
            await self.socket.close()
            self.socket = None

    async def close(self):
        await self.disconnect()

    async def _send_wrapper(self, proc):
        count = 0
        while True:
            try:
                await self.connect()
                return await proc()
            except (
                OSError,
                ConnectionError,
                ConnectionClosedError,
                json.JSONDecodeError,
                UnicodeDecodeError,
            ) as e:
                self.logger.warning("Error talking to server: %s" % e)
                if count >= 3:
                    if not isinstance(e, ConnectionError):
                        raise ConnectionError(str(e))
                    raise e
                await self.close()
                count += 1

    def check_invoke_error(self, msg):
        if isinstance(msg, dict) and "invoke-error" in msg:
            raise InvokeError(msg["invoke-error"]["message"])

    async def invoke(self, msg):
        async def proc():
            await self.socket.send_message(msg)
            return await self.socket.recv_message()

        result = await self._send_wrapper(proc)
        self.check_invoke_error(result)
        return result

    async def ping(self):
        return await self.invoke({"ping": {}})

    async def __aenter__(self):
        return self

    async def __aexit__(self, exc_type, exc_value, traceback):
        await self.close()


class Client(object):
    def __init__(self):
        self.client = self._get_async_client()
        self.loop = asyncio.new_event_loop()

        # Override any pre-existing loop.
        # Without this, the PR server export selftest triggers a hang
        # when running with Python 3.7.  The drawback is that there is
        # potential for issues if the PR and hash equiv (or some new)
        # clients need to both be instantiated in the same process.
        # This should be revisited if/when Python 3.9 becomes the
        # minimum required version for BitBake, as it seems not
        # required (but harmless) with it.
        asyncio.set_event_loop(self.loop)

        self._add_methods("connect_tcp", "ping")

    @abc.abstractmethod
    def _get_async_client(self):
        pass

    def _get_downcall_wrapper(self, downcall):
        def wrapper(*args, **kwargs):
            return self.loop.run_until_complete(downcall(*args, **kwargs))

        return wrapper

    def _add_methods(self, *methods):
        for m in methods:
            downcall = getattr(self.client, m)
            setattr(self, m, self._get_downcall_wrapper(downcall))

    def connect_unix(self, path):
        self.loop.run_until_complete(self.client.connect_unix(path))
        self.loop.run_until_complete(self.client.connect())

    @property
    def max_chunk(self):
        return self.client.max_chunk

    @max_chunk.setter
    def max_chunk(self, value):
        self.client.max_chunk = value

    def disconnect(self):
        self.loop.run_until_complete(self.client.close())

    def close(self):
        if self.loop:
            self.loop.run_until_complete(self.client.close())
            self.loop.run_until_complete(self.loop.shutdown_asyncgens())
            self.loop.close()
        self.loop = None

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        self.close()
        return False
