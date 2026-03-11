#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#


class ClientError(Exception):
    pass


class InvokeError(Exception):
    pass


class ServerError(Exception):
    pass


class ConnectionClosedError(Exception):
    pass
