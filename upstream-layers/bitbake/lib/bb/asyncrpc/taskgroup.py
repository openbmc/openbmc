#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#
import asyncio
import logging

logger = logging.getLogger("asyncio.TaskGroup")


class TaskGroup(object):
    def __init__(self):
        self._tasks = []

    def create_task(self, coro, **kwargs):
        self._tasks.append(asyncio.create_task(coro, **kwargs))

    async def __aenter__(self):
        return self

    async def __aexit__(self, exc_type, exc, tb):
        try:
            if exc is None:
                while self._tasks:
                    done, pending = await asyncio.wait(
                        self._tasks, return_when=asyncio.FIRST_COMPLETED
                    )
                    self._tasks = pending

                    for t in done:
                        try:
                            await t
                        except asyncio.CancelledError:
                            pass

        finally:
            for t in self._tasks:
                t.cancel()
                try:
                    await t
                except:
                    # Ignore exceptions
                    pass

        return False

    @classmethod
    async def run(cls, *coros):
        async with cls() as group:
            for c in coros:
                group.create_task(c)
