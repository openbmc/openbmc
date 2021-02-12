# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import asyncio
import json
import logging
import socket
import os
from . import chunkify, DEFAULT_MAX_CHUNK, create_async_client


logger = logging.getLogger("hashserv.client")


class HashConnectionError(Exception):
    pass


class AsyncClient(object):
    MODE_NORMAL = 0
    MODE_GET_STREAM = 1

    def __init__(self):
        self.reader = None
        self.writer = None
        self.mode = self.MODE_NORMAL
        self.max_chunk = DEFAULT_MAX_CHUNK

    async def connect_tcp(self, address, port):
        async def connect_sock():
            return await asyncio.open_connection(address, port)

        self._connect_sock = connect_sock

    async def connect_unix(self, path):
        async def connect_sock():
            return await asyncio.open_unix_connection(path)

        self._connect_sock = connect_sock

    async def connect(self):
        if self.reader is None or self.writer is None:
            (self.reader, self.writer) = await self._connect_sock()

            self.writer.write("OEHASHEQUIV 1.1\n\n".encode("utf-8"))
            await self.writer.drain()

            cur_mode = self.mode
            self.mode = self.MODE_NORMAL
            await self._set_mode(cur_mode)

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
                HashConnectionError,
                json.JSONDecodeError,
                UnicodeDecodeError,
            ) as e:
                logger.warning("Error talking to server: %s" % e)
                if count >= 3:
                    if not isinstance(e, HashConnectionError):
                        raise HashConnectionError(str(e))
                    raise e
                await self.close()
                count += 1

    async def send_message(self, msg):
        async def get_line():
            line = await self.reader.readline()
            if not line:
                raise HashConnectionError("Connection closed")

            line = line.decode("utf-8")

            if not line.endswith("\n"):
                raise HashConnectionError("Bad message %r" % message)

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

    async def send_stream(self, msg):
        async def proc():
            self.writer.write(("%s\n" % msg).encode("utf-8"))
            await self.writer.drain()
            l = await self.reader.readline()
            if not l:
                raise HashConnectionError("Connection closed")
            return l.decode("utf-8").rstrip()

        return await self._send_wrapper(proc)

    async def _set_mode(self, new_mode):
        if new_mode == self.MODE_NORMAL and self.mode == self.MODE_GET_STREAM:
            r = await self.send_stream("END")
            if r != "ok":
                raise HashConnectionError("Bad response from server %r" % r)
        elif new_mode == self.MODE_GET_STREAM and self.mode == self.MODE_NORMAL:
            r = await self.send_message({"get-stream": None})
            if r != "ok":
                raise HashConnectionError("Bad response from server %r" % r)
        elif new_mode != self.mode:
            raise Exception(
                "Undefined mode transition %r -> %r" % (self.mode, new_mode)
            )

        self.mode = new_mode

    async def get_unihash(self, method, taskhash):
        await self._set_mode(self.MODE_GET_STREAM)
        r = await self.send_stream("%s %s" % (method, taskhash))
        if not r:
            return None
        return r

    async def report_unihash(self, taskhash, method, outhash, unihash, extra={}):
        await self._set_mode(self.MODE_NORMAL)
        m = extra.copy()
        m["taskhash"] = taskhash
        m["method"] = method
        m["outhash"] = outhash
        m["unihash"] = unihash
        return await self.send_message({"report": m})

    async def report_unihash_equiv(self, taskhash, method, unihash, extra={}):
        await self._set_mode(self.MODE_NORMAL)
        m = extra.copy()
        m["taskhash"] = taskhash
        m["method"] = method
        m["unihash"] = unihash
        return await self.send_message({"report-equiv": m})

    async def get_taskhash(self, method, taskhash, all_properties=False):
        await self._set_mode(self.MODE_NORMAL)
        return await self.send_message(
            {"get": {"taskhash": taskhash, "method": method, "all": all_properties}}
        )

    async def get_outhash(self, method, outhash, taskhash):
        await self._set_mode(self.MODE_NORMAL)
        return await self.send_message(
            {"get-outhash": {"outhash": outhash, "taskhash": taskhash, "method": method}}
        )

    async def get_stats(self):
        await self._set_mode(self.MODE_NORMAL)
        return await self.send_message({"get-stats": None})

    async def reset_stats(self):
        await self._set_mode(self.MODE_NORMAL)
        return await self.send_message({"reset-stats": None})

    async def backfill_wait(self):
        await self._set_mode(self.MODE_NORMAL)
        return (await self.send_message({"backfill-wait": None}))["tasks"]


class Client(object):
    def __init__(self):
        self.client = AsyncClient()
        self.loop = asyncio.new_event_loop()

        for call in (
            "connect_tcp",
            "close",
            "get_unihash",
            "report_unihash",
            "report_unihash_equiv",
            "get_taskhash",
            "get_stats",
            "reset_stats",
            "backfill_wait",
        ):
            downcall = getattr(self.client, call)
            setattr(self, call, self._get_downcall_wrapper(downcall))

    def _get_downcall_wrapper(self, downcall):
        def wrapper(*args, **kwargs):
            return self.loop.run_until_complete(downcall(*args, **kwargs))

        return wrapper

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
