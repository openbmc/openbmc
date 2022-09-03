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
        super().__init__('PRSERVICE', '1.0', logger)

    async def getPR(self, version, pkgarch, checksum):
        response = await self.send_message(
            {'get-pr': {'version': version, 'pkgarch': pkgarch, 'checksum': checksum}}
        )
        if response:
            return response['value']

    async def importone(self, version, pkgarch, checksum, value):
        response = await self.send_message(
            {'import-one': {'version': version, 'pkgarch': pkgarch, 'checksum': checksum, 'value': value}}
        )
        if response:
            return response['value']

    async def export(self, version, pkgarch, checksum, colinfo):
        response = await self.send_message(
            {'export': {'version': version, 'pkgarch': pkgarch, 'checksum': checksum, 'colinfo': colinfo}}
        )
        if response:
            return (response['metainfo'], response['datainfo'])

    async def is_readonly(self):
        response = await self.send_message(
            {'is-readonly': {}}
        )
        if response:
            return response['readonly']

class PRClient(bb.asyncrpc.Client):
    def __init__(self):
        super().__init__()
        self._add_methods('getPR', 'importone', 'export', 'is_readonly')

    def _get_async_client(self):
        return PRAsyncClient()
