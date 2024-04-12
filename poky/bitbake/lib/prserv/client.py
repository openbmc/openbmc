#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import logging
import bb.asyncrpc

logger = logging.getLogger("BitBake.PRserv")

class PRAsyncClient(bb.asyncrpc.AsyncClient):
    def __init__(self):
        super().__init__("PRSERVICE", "1.0", logger)

    async def getPR(self, version, pkgarch, checksum):
        response = await self.invoke(
            {"get-pr": {"version": version, "pkgarch": pkgarch, "checksum": checksum}}
        )
        if response:
            return response["value"]

    async def test_pr(self, version, pkgarch, checksum):
        response = await self.invoke(
            {"test-pr": {"version": version, "pkgarch": pkgarch, "checksum": checksum}}
        )
        if response:
            return response["value"]

    async def test_package(self, version, pkgarch):
        response = await self.invoke(
            {"test-package": {"version": version, "pkgarch": pkgarch}}
        )
        if response:
            return response["value"]

    async def max_package_pr(self, version, pkgarch):
        response = await self.invoke(
            {"max-package-pr": {"version": version, "pkgarch": pkgarch}}
        )
        if response:
            return response["value"]

    async def importone(self, version, pkgarch, checksum, value):
        response = await self.invoke(
            {"import-one": {"version": version, "pkgarch": pkgarch, "checksum": checksum, "value": value}}
        )
        if response:
            return response["value"]

    async def export(self, version, pkgarch, checksum, colinfo):
        response = await self.invoke(
            {"export": {"version": version, "pkgarch": pkgarch, "checksum": checksum, "colinfo": colinfo}}
        )
        if response:
            return (response["metainfo"], response["datainfo"])

    async def is_readonly(self):
        response = await self.invoke(
            {"is-readonly": {}}
        )
        if response:
            return response["readonly"]

class PRClient(bb.asyncrpc.Client):
    def __init__(self):
        super().__init__()
        self._add_methods("getPR", "test_pr", "test_package", "importone", "export", "is_readonly")

    def _get_async_client(self):
        return PRAsyncClient()
