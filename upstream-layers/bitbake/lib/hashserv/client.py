# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import socket
import asyncio
import bb.asyncrpc
import json
from abc import abstractmethod
from collections.abc import AsyncIterable
from contextlib import asynccontextmanager
from dataclasses import dataclass
from . import create_async_client

logger = logging.getLogger("hashserv.client")


class AsyncQueue(AsyncIterable):
    class Shutdown(Exception):
        pass

    SHUTDOWN_SENTINEL = object()

    def __init__(self, *args, **kwargs):
        self.__queue = asyncio.Queue()
        self.__shutdown = False
        self.__is_done = False

    async def done(self):
        if self.__is_done:
            return
        self.__is_done = True
        await self.__queue.put(self.SHUTDOWN_SENTINEL)

    async def put(self, item):
        if self.__is_done:
            raise self.Shutdown
        await self.__queue.put(item)

    async def get(self):
        if self.__shutdown:
            raise self.Shutdown

        item = await self.__queue.get()
        if item is self.SHUTDOWN_SENTINEL:
            self.__shutdown = True
            raise self.Shutdown

        return item

    def __aiter__(self):
        return self

    async def __anext__(self):
        try:
            return await self.get()
        except self.Shutdown:
            raise StopAsyncIteration


@dataclass(eq=False, frozen=True)
class AsyncPipe:
    send_queue: AsyncQueue
    recv_queue: AsyncQueue


class Stream(AsyncIterable):
    def __init__(self, pipe):
        self._pipe = pipe

    async def done(self):
        await self._pipe.send_queue.done()

    def __aiter__(self):
        return self

    async def __anext__(self):
        try:
            return await self.get_result()
        except AsyncQueue.Shutdown:
            raise StopAsyncIteration

    @abstractmethod
    async def _send_batch_input(self, i):
        raise NotImplementedError("Not implemented")

    @abstractmethod
    async def get_result(self):
        raise NotImplementedError("Not implemented")

    async def batch(self, inputs):
        """
        Does a "batch" process of stream messages. This sends the query
        messages as fast as possible, and simultaneously attempts to read the
        messages back. This helps to mitigate the effects of latency to the
        hash equivalence server be allowing multiple queries to be "in-flight"
        at once

        The input may be a generator or an async generator
        """

        async def get_inputs():
            if isinstance(inputs, AsyncIterable):
                async for i in inputs:
                    yield i
            else:
                for i in inputs:
                    yield i

        async def send():
            try:
                async for i in get_inputs():
                    await self._send_batch_input(i)
            finally:
                await self.done()

        results = []

        async def recv():
            async for item in self:
                results.append(item)

        await bb.asyncrpc.TaskGroup.run(send(), recv())
        return results


class GetUnihashStream(Stream):
    def __init__(self, pipe):
        super().__init__(pipe)

    async def _send_batch_input(self, i):
        method, taskhash = i
        await self.send_query(method, taskhash)

    async def send_query(self, method, taskhash):
        await self._pipe.send_queue.put(f"{method} {taskhash}")

    async def get_result(self):
        r = await self._pipe.recv_queue.get()
        return r if r else None


class UnihashExistsStream(Stream):
    def __init__(self, pipe):
        super().__init__(pipe)

    async def _send_batch_input(self, i):
        await self.send_query(i)

    async def send_query(self, unihash):
        await self._pipe.send_queue.put(unihash)

    async def get_result(self):
        r = await self._pipe.recv_queue.get()
        return r == "true"


class GcMarkStream(Stream):
    def __init__(self, pipe, mark):
        super().__init__(pipe)
        self.mark = mark

    async def _send_batch_input(self, i):
        def row_to_dict(row):
            pairs = row.split()
            return dict(zip(pairs[::2], pairs[1::2]))

        await self.send_mark(row_to_dict(i))

    async def send_mark(self, where):
        await self._pipe.send_queue.put(json.dumps({"mark": self.mark, "where": where}))

    async def get_result(self):
        r = await self._pipe.recv_queue.get()
        return json.loads(r)


class Batch(object):
    def __init__(self, send_queue, recv_queue):
        self.send_queue = send_queue
        self.recv_queue = recv_queue
        self.fill_done = False
        self.send_done = False
        self.cond = asyncio.Condition()
        self.pending = []
        self.sent_count = 0
        self.recv_count = 0
        self.item = None

    async def recv(self, socket):
        while True:
            async with self.cond:
                await self.cond.wait_for(lambda: self.pending or self.send_done)
                if not self.pending:
                    if self.send_done:
                        return
                    continue

            m = await socket.recv()
            await self.recv_queue.put(m)

            async with self.cond:
                self.recv_count += 1
                self.pending.pop(0)

    async def fill(self):
        async for m in self.send_queue:
            async with self.cond:
                # Wait for item to be consumed
                await self.cond.wait_for(lambda: self.item is None)
                self.item = m
                self.cond.notify_all()

        async with self.cond:
            self.fill_done = True
            self.cond.notify_all()

    async def send(self, socket):
        # In the event of a restart due to a reconnect, all in-flight
        # messages need to be resent first to keep to result count in sync
        async with self.cond:
            for m in self.pending:
                await socket.send(m)

        while True:
            async with self.cond:
                await self.cond.wait_for(
                    lambda: self.item is not None or self.fill_done
                )
                if self.item is None:
                    if self.fill_done:
                        self.send_done = True
                        self.cond.notify_all()
                        return
                    continue

                m = self.item

            await socket.send(m)

            async with self.cond:
                self.item = None
                self.pending.append(m)
                self.sent_count += 1
                self.cond.notify_all()

    async def stream(self, socket):
        await bb.asyncrpc.TaskGroup.run(self.send(socket), self.recv(socket))

    def check(self):
        if self.sent_count != self.recv_count:
            raise ConnectionError(
                f"Sent {self.sent_count} messages but only received {self.recv_count}"
            )


class AsyncClient(bb.asyncrpc.AsyncClient):
    MODE_NORMAL = 0
    MODE_GET_STREAM = 1
    MODE_EXIST_STREAM = 2
    MODE_MARK_STREAM = 3

    def __init__(self, username=None, password=None):
        super().__init__("OEHASHEQUIV", "1.1", logger)
        self.mode = self.MODE_NORMAL
        self.username = username
        self.password = password
        self.saved_become_user = None

    async def setup_connection(self):
        await super().setup_connection()
        self.mode = self.MODE_NORMAL
        if self.username:
            # Save off become user temporarily because auth() resets it
            become = self.saved_become_user
            await self.auth(self.username, self.password)

            if become:
                await self.become_user(become)

    @asynccontextmanager
    async def send_stream(self, mode):
        send_queue = AsyncQueue()
        recv_queue = AsyncQueue()
        b = Batch(send_queue, recv_queue)

        async def proc():
            await self._set_mode(mode)
            await b.stream(self.socket)

        async def process():
            try:
                await self._send_wrapper(proc)
            finally:
                await recv_queue.done()

        # Create background process to process messages
        async with bb.asyncrpc.TaskGroup() as group:
            group.create_task(process())
            group.create_task(b.fill())
            try:
                yield AsyncPipe(send_queue, recv_queue)
                b.check()
            except AsyncQueue.Shutdown as e:
                pass
            finally:
                await send_queue.done()
                await recv_queue.done()

    async def invoke(self, *args, skip_mode=False, **kwargs):
        # It's OK if connection errors cause a failure here, because the mode
        # is also reset to normal on a new connection
        if not skip_mode:
            await self._set_mode(self.MODE_NORMAL)
        return await super().invoke(*args, **kwargs)

    async def _set_mode(self, new_mode):
        async def stream_to_normal():
            # Check if already in normal mode (e.g. due to a connection reset)
            if self.mode == self.MODE_NORMAL:
                return "ok"
            await self.socket.send("END")
            return await self.socket.recv()

        async def normal_to_stream(command):
            r = await self.invoke({command: None}, skip_mode=True)
            if r != "ok":
                self.check_invoke_error(r)
                raise ConnectionError(
                    f"Unable to transition to stream mode: Bad response from server {r!r}"
                )
            self.logger.debug("Mode is now %s", command)

        if new_mode == self.mode:
            return

        self.logger.debug("Transitioning mode %s -> %s", self.mode, new_mode)

        # Always transition to normal mode before switching to any other mode
        if self.mode != self.MODE_NORMAL:
            r = await self._send_wrapper(stream_to_normal)
            if r != "ok":
                self.check_invoke_error(r)
                raise ConnectionError(
                    f"Unable to transition to normal mode: Bad response from server {r!r}"
                )
            self.logger.debug("Mode is now normal")

        if new_mode == self.MODE_GET_STREAM:
            await normal_to_stream("get-stream")
        elif new_mode == self.MODE_EXIST_STREAM:
            await normal_to_stream("exists-stream")
        elif new_mode == self.MODE_MARK_STREAM:
            await normal_to_stream("gc-mark-stream")
        elif new_mode != self.MODE_NORMAL:
            raise Exception("Undefined mode transition {self.mode!r} -> {new_mode!r}")

        self.mode = new_mode

    async def get_unihash(self, method, taskhash):
        async with self.get_unihash_stream() as stream:
            await stream.send_query(method, taskhash)
            return await stream.get_result()

    async def get_unihash_batch(self, args):
        async with self.get_unihash_stream() as stream:
            return await stream.batch(args)

    @asynccontextmanager
    async def get_unihash_stream(self):
        async with self.send_stream(self.MODE_GET_STREAM) as pipe:
            yield GetUnihashStream(pipe)

    async def report_unihash(self, taskhash, method, outhash, unihash, extra={}):
        m = extra.copy()
        m["taskhash"] = taskhash
        m["method"] = method
        m["outhash"] = outhash
        m["unihash"] = unihash
        return await self.invoke({"report": m})

    async def report_unihash_equiv(self, taskhash, method, unihash, extra={}):
        m = extra.copy()
        m["taskhash"] = taskhash
        m["method"] = method
        m["unihash"] = unihash
        return await self.invoke({"report-equiv": m})

    async def get_taskhash(self, method, taskhash, all_properties=False):
        return await self.invoke(
            {"get": {"taskhash": taskhash, "method": method, "all": all_properties}}
        )

    async def unihash_exists(self, unihash):
        async with self.unihash_exists_stream() as stream:
            await stream.send_query(unihash)
            return await stream.get_result()

    async def unihash_exists_batch(self, unihashes):
        async with self.unihash_exists_stream() as stream:
            return await stream.batch(unihashes)

    @asynccontextmanager
    async def unihash_exists_stream(self):
        async with self.send_stream(self.MODE_EXIST_STREAM) as pipe:
            yield UnihashExistsStream(pipe)

    async def get_outhash(self, method, outhash, taskhash, with_unihash=True):
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
        return await self.invoke({"get-stats": None})

    async def reset_stats(self):
        return await self.invoke({"reset-stats": None})

    async def backfill_wait(self):
        return (await self.invoke({"backfill-wait": None}))["tasks"]

    async def remove(self, where):
        return await self.invoke({"remove": {"where": where}})

    async def clean_unused(self, max_age):
        return await self.invoke({"clean-unused": {"max_age_seconds": max_age}})

    async def auth(self, username, token):
        result = await self.invoke({"auth": {"username": username, "token": token}})
        self.username = username
        self.password = token
        self.saved_become_user = None
        return result

    async def refresh_token(self, username=None):
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
        return await self.invoke(
            {"set-user-perms": {"username": username, "permissions": permissions}}
        )

    async def get_user(self, username=None):
        m = {}
        if username:
            m["username"] = username
        return await self.invoke({"get-user": m})

    async def get_all_users(self):
        return (await self.invoke({"get-all-users": {}}))["users"]

    async def new_user(self, username, permissions):
        return await self.invoke(
            {"new-user": {"username": username, "permissions": permissions}}
        )

    async def delete_user(self, username):
        return await self.invoke({"delete-user": {"username": username}})

    async def become_user(self, username):
        result = await self.invoke({"become-user": {"username": username}})
        if username == self.username:
            self.saved_become_user = None
        else:
            self.saved_become_user = username
        return result

    async def get_db_usage(self):
        return (await self.invoke({"get-db-usage": {}}))["usage"]

    async def get_db_query_columns(self):
        return (await self.invoke({"get-db-query-columns": {}}))["columns"]

    async def gc_status(self):
        return await self.invoke({"gc-status": {}})

    async def gc_mark(self, mark, where):
        """
        Starts a new garbage collection operation identified by "mark". If
        garbage collection is already in progress with "mark", the collection
        is continued.

        All unihash entries that match the "where" clause are marked to be
        kept. In addition, any new entries added to the database after this
        command will be automatically marked with "mark"
        """
        return await self.invoke({"gc-mark": {"mark": mark, "where": where}})

    async def gc_mark_batch(self, mark, rows):
        """
        Similar to `gc-mark`, but accepts a list of "where" key-value pair
        conditions. It utilizes stream mode to mark hashes, which helps reduce
        the impact of latency when communicating with the hash equivalence
        server.
        """
        async with self.gc_mark_stream(mark) as stream:
            results = await stream.batch(rows)

        return {"count": sum(int(r["count"]) for r in results)}

    @asynccontextmanager
    async def gc_mark_stream(self, mark):
        async with self.send_stream(self.MODE_MARK_STREAM) as pipe:
            yield GcMarkStream(pipe, mark)

    async def gc_sweep(self, mark):
        """
        Finishes garbage collection for "mark". All unihash entries that have
        not been marked will be deleted.

        It is recommended to clean unused outhash entries after running this to
        cleanup any dangling outhashes
        """
        return await self.invoke({"gc-sweep": {"mark": mark}})


class Client(bb.asyncrpc.Client):
    def __init__(self, username=None, password=None):
        self.username = username
        self.password = password

        super().__init__()
        self._add_methods(
            "connect_tcp",
            "connect_websocket",
            "get_unihash",
            "get_unihash_batch",
            "report_unihash",
            "report_unihash_equiv",
            "get_taskhash",
            "unihash_exists",
            "unihash_exists_batch",
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
            "gc_status",
            "gc_mark",
            "gc_mark_batch",
            "gc_sweep",
        )

    def _get_async_client(self):
        return AsyncClient(self.username, self.password)
