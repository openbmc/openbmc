# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import socket
import bb.asyncrpc
from . import create_async_client


logger = logging.getLogger("hashserv.client")


class AsyncClient(bb.asyncrpc.AsyncClient):
    MODE_NORMAL = 0
    MODE_GET_STREAM = 1

    def __init__(self):
        super().__init__('OEHASHEQUIV', '1.1', logger)
        self.mode = self.MODE_NORMAL

    async def setup_connection(self):
        await super().setup_connection()
        cur_mode = self.mode
        self.mode = self.MODE_NORMAL
        await self._set_mode(cur_mode)

    async def send_stream(self, msg):
        async def proc():
            self.writer.write(("%s\n" % msg).encode("utf-8"))
            await self.writer.drain()
            l = await self.reader.readline()
            if not l:
                raise ConnectionError("Connection closed")
            return l.decode("utf-8").rstrip()

        return await self._send_wrapper(proc)

    async def _set_mode(self, new_mode):
        if new_mode == self.MODE_NORMAL and self.mode == self.MODE_GET_STREAM:
            r = await self.send_stream("END")
            if r != "ok":
                raise ConnectionError("Bad response from server %r" % r)
        elif new_mode == self.MODE_GET_STREAM and self.mode == self.MODE_NORMAL:
            r = await self.send_message({"get-stream": None})
            if r != "ok":
                raise ConnectionError("Bad response from server %r" % r)
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


class Client(bb.asyncrpc.Client):
    def __init__(self):
        super().__init__()
        self._add_methods(
            "connect_tcp",
            "get_unihash",
            "report_unihash",
            "report_unihash_equiv",
            "get_taskhash",
            "get_outhash",
            "get_stats",
            "reset_stats",
            "backfill_wait",
        )

    def _get_async_client(self):
        return AsyncClient()
