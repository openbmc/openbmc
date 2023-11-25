# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import socket
import bb.asyncrpc
import json
from . import create_async_client


logger = logging.getLogger("hashserv.client")


class AsyncClient(bb.asyncrpc.AsyncClient):
    MODE_NORMAL = 0
    MODE_GET_STREAM = 1

    def __init__(self, username=None, password=None):
        super().__init__("OEHASHEQUIV", "1.1", logger)
        self.mode = self.MODE_NORMAL
        self.username = username
        self.password = password
        self.saved_become_user = None

    async def setup_connection(self):
        await super().setup_connection()
        cur_mode = self.mode
        self.mode = self.MODE_NORMAL
        await self._set_mode(cur_mode)
        if self.username:
            # Save off become user temporarily because auth() resets it
            become = self.saved_become_user
            await self.auth(self.username, self.password)

            if become:
                await self.become_user(become)

    async def send_stream(self, msg):
        async def proc():
            await self.socket.send(msg)
            return await self.socket.recv()

        return await self._send_wrapper(proc)

    async def _set_mode(self, new_mode):
        async def stream_to_normal():
            await self.socket.send("END")
            return await self.socket.recv()

        if new_mode == self.MODE_NORMAL and self.mode == self.MODE_GET_STREAM:
            r = await self._send_wrapper(stream_to_normal)
            if r != "ok":
                self.check_invoke_error(r)
                raise ConnectionError("Unable to transition to normal mode: Bad response from server %r" % r)
        elif new_mode == self.MODE_GET_STREAM and self.mode == self.MODE_NORMAL:
            r = await self.invoke({"get-stream": None})
            if r != "ok":
                raise ConnectionError("Unable to transition to stream mode: Bad response from server %r" % r)
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
        return await self.invoke({"report": m})

    async def report_unihash_equiv(self, taskhash, method, unihash, extra={}):
        await self._set_mode(self.MODE_NORMAL)
        m = extra.copy()
        m["taskhash"] = taskhash
        m["method"] = method
        m["unihash"] = unihash
        return await self.invoke({"report-equiv": m})

    async def get_taskhash(self, method, taskhash, all_properties=False):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke(
            {"get": {"taskhash": taskhash, "method": method, "all": all_properties}}
        )

    async def get_outhash(self, method, outhash, taskhash, with_unihash=True):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke(
            {
                "get-outhash": {
                    "outhash": outhash,
                    "taskhash": taskhash,
                    "method": method,
                    "with_unihash": with_unihash,
                }
            }
        )

    async def get_stats(self):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke({"get-stats": None})

    async def reset_stats(self):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke({"reset-stats": None})

    async def backfill_wait(self):
        await self._set_mode(self.MODE_NORMAL)
        return (await self.invoke({"backfill-wait": None}))["tasks"]

    async def remove(self, where):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke({"remove": {"where": where}})

    async def clean_unused(self, max_age):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke({"clean-unused": {"max_age_seconds": max_age}})

    async def auth(self, username, token):
        await self._set_mode(self.MODE_NORMAL)
        result = await self.invoke({"auth": {"username": username, "token": token}})
        self.username = username
        self.password = token
        self.saved_become_user = None
        return result

    async def refresh_token(self, username=None):
        await self._set_mode(self.MODE_NORMAL)
        m = {}
        if username:
            m["username"] = username
        result = await self.invoke({"refresh-token": m})
        if (
            self.username
            and not self.saved_become_user
            and result["username"] == self.username
        ):
            self.password = result["token"]
        return result

    async def set_user_perms(self, username, permissions):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke(
            {"set-user-perms": {"username": username, "permissions": permissions}}
        )

    async def get_user(self, username=None):
        await self._set_mode(self.MODE_NORMAL)
        m = {}
        if username:
            m["username"] = username
        return await self.invoke({"get-user": m})

    async def get_all_users(self):
        await self._set_mode(self.MODE_NORMAL)
        return (await self.invoke({"get-all-users": {}}))["users"]

    async def new_user(self, username, permissions):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke(
            {"new-user": {"username": username, "permissions": permissions}}
        )

    async def delete_user(self, username):
        await self._set_mode(self.MODE_NORMAL)
        return await self.invoke({"delete-user": {"username": username}})

    async def become_user(self, username):
        await self._set_mode(self.MODE_NORMAL)
        result = await self.invoke({"become-user": {"username": username}})
        if username == self.username:
            self.saved_become_user = None
        else:
            self.saved_become_user = username
        return result

    async def get_db_usage(self):
        await self._set_mode(self.MODE_NORMAL)
        return (await self.invoke({"get-db-usage": {}}))["usage"]

    async def get_db_query_columns(self):
        await self._set_mode(self.MODE_NORMAL)
        return (await self.invoke({"get-db-query-columns": {}}))["columns"]


class Client(bb.asyncrpc.Client):
    def __init__(self, username=None, password=None):
        self.username = username
        self.password = password

        super().__init__()
        self._add_methods(
            "connect_tcp",
            "connect_websocket",
            "get_unihash",
            "report_unihash",
            "report_unihash_equiv",
            "get_taskhash",
            "get_outhash",
            "get_stats",
            "reset_stats",
            "backfill_wait",
            "remove",
            "clean_unused",
            "auth",
            "refresh_token",
            "set_user_perms",
            "get_user",
            "get_all_users",
            "new_user",
            "delete_user",
            "become_user",
            "get_db_usage",
            "get_db_query_columns",
        )

    def _get_async_client(self):
        return AsyncClient(self.username, self.password)
