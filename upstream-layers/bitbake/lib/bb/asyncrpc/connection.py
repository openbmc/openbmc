#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import asyncio
import itertools
import json
from datetime import datetime
from .exceptions import ClientError, ConnectionClosedError


# The Python async server defaults to a 64K receive buffer, so we hardcode our
# maximum chunk size. It would be better if the client and server reported to
# each other what the maximum chunk sizes were, but that will slow down the
# connection setup with a round trip delay so I'd rather not do that unless it
# is necessary
DEFAULT_MAX_CHUNK = 32 * 1024


def chunkify(msg, max_chunk):
    if len(msg) < max_chunk - 1:
        yield "".join((msg, "\n"))
    else:
        yield "".join((json.dumps({"chunk-stream": None}), "\n"))

        args = [iter(msg)] * (max_chunk - 1)
        for m in map("".join, itertools.zip_longest(*args, fillvalue="")):
            yield "".join(itertools.chain(m, "\n"))
        yield "\n"


def json_serialize(obj):
    if isinstance(obj, datetime):
        return obj.isoformat()
    raise TypeError("Type %s not serializeable" % type(obj))


class StreamConnection(object):
    def __init__(self, reader, writer, timeout, max_chunk=DEFAULT_MAX_CHUNK):
        self.reader = reader
        self.writer = writer
        self.timeout = timeout
        self.max_chunk = max_chunk

    @property
    def address(self):
        return self.writer.get_extra_info("peername")

    async def send_message(self, msg):
        for c in chunkify(json.dumps(msg, default=json_serialize), self.max_chunk):
            self.writer.write(c.encode("utf-8"))
        await self.writer.drain()

    async def recv_message(self):
        l = await self.recv()

        m = json.loads(l)
        if not m:
            return m

        if "chunk-stream" in m:
            lines = []
            while True:
                l = await self.recv()
                if not l:
                    break
                lines.append(l)

            m = json.loads("".join(lines))

        return m

    async def send(self, msg):
        self.writer.write(("%s\n" % msg).encode("utf-8"))
        await self.writer.drain()

    async def recv(self):
        if self.timeout < 0:
            line = await self.reader.readline()
        else:
            try:
                line = await asyncio.wait_for(self.reader.readline(), self.timeout)
            except asyncio.TimeoutError:
                raise ConnectionError("Timed out waiting for data")

        if not line:
            raise ConnectionClosedError("Connection closed")

        line = line.decode("utf-8")

        if not line.endswith("\n"):
            raise ConnectionError("Bad message %r" % (line))

        return line.rstrip()

    async def close(self):
        self.reader = None
        if self.writer is not None:
            self.writer.close()
            self.writer = None


class WebsocketConnection(object):
    def __init__(self, socket, timeout):
        self.socket = socket
        self.timeout = timeout

    @property
    def address(self):
        return ":".join(str(s) for s in self.socket.remote_address)

    async def send_message(self, msg):
        await self.send(json.dumps(msg, default=json_serialize))

    async def recv_message(self):
        m = await self.recv()
        return json.loads(m)

    async def send(self, msg):
        import websockets.exceptions

        try:
            await self.socket.send(msg)
        except websockets.exceptions.ConnectionClosed:
            raise ConnectionClosedError("Connection closed")

    async def recv(self):
        import websockets.exceptions

        try:
            if self.timeout < 0:
                return await self.socket.recv()

            try:
                return await asyncio.wait_for(self.socket.recv(), self.timeout)
            except asyncio.TimeoutError:
                raise ConnectionError("Timed out waiting for data")
        except websockets.exceptions.ConnectionClosed:
            raise ConnectionClosedError("Connection closed")

    async def close(self):
        if self.socket is not None:
            await self.socket.close()
            self.socket = None
