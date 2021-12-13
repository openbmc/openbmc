#
# SPDX-License-Identifier: GPL-2.0-only
#

import abc
import asyncio
import json
import os
import socket
import sys
from . import chunkify, DEFAULT_MAX_CHUNK


class AsyncClient(object):
    def __init__(self, proto_name, proto_version, logger, timeout=30):
        self.reader = None
        self.writer = None
        self.max_chunk = DEFAULT_MAX_CHUNK
        self.proto_name = proto_name
        self.proto_version = proto_version
        self.logger = logger
        self.timeout = timeout

    async def connect_tcp(self, address, port):
        async def connect_sock():
            return await asyncio.open_connection(address, port)

        self._connect_sock = connect_sock

    async def connect_unix(self, path):
        async def connect_sock():
            return await asyncio.open_unix_connection(path)

        self._connect_sock = connect_sock

    async def setup_connection(self):
        s = '%s %s\n\n' % (self.proto_name, self.proto_version)
        self.writer.write(s.encode("utf-8"))
        await self.writer.drain()

    async def connect(self):
        if self.reader is None or self.writer is None:
            (self.reader, self.writer) = await self._connect_sock()
            await self.setup_connection()

    async def close(self):
        self.reader = None

        if self.writer is not None:
            self.writer.close()
            self.writer = None

    async def _send_wrapper(self, proc):
        count = 0
        while True:
            try:
                await self.connect()
                return await proc()
            except (
                OSError,
                ConnectionError,
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

    async def send_message(self, msg):
        async def get_line():
            try:
                line = await asyncio.wait_for(self.reader.readline(), self.timeout)
            except asyncio.TimeoutError:
                raise ConnectionError("Timed out waiting for server")

            if not line:
                raise ConnectionError("Connection closed")

            line = line.decode("utf-8")

            if not line.endswith("\n"):
                raise ConnectionError("Bad message %r" % (line))

            return line

        async def proc():
            for c in chunkify(json.dumps(msg), self.max_chunk):
                self.writer.write(c.encode("utf-8"))
            await self.writer.drain()

            l = await get_line()

            m = json.loads(l)
            if m and "chunk-stream" in m:
                lines = []
                while True:
                    l = (await get_line()).rstrip("\n")
                    if not l:
                        break
                    lines.append(l)

                m = json.loads("".join(lines))

            return m

        return await self._send_wrapper(proc)

    async def ping(self):
        return await self.send_message(
            {'ping': {}}
        )


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

        self._add_methods('connect_tcp', 'ping')

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
        # AF_UNIX has path length issues so chdir here to workaround
        cwd = os.getcwd()
        try:
            os.chdir(os.path.dirname(path))
            self.loop.run_until_complete(self.client.connect_unix(os.path.basename(path)))
            self.loop.run_until_complete(self.client.connect())
        finally:
            os.chdir(cwd)

    @property
    def max_chunk(self):
        return self.client.max_chunk

    @max_chunk.setter
    def max_chunk(self, value):
        self.client.max_chunk = value

    def close(self):
        self.loop.run_until_complete(self.client.close())
        if sys.version_info >= (3, 6):
            self.loop.run_until_complete(self.loop.shutdown_asyncgens())
        self.loop.close()
