#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import itertools
import json

# The Python async server defaults to a 64K receive buffer, so we hardcode our
# maximum chunk size. It would be better if the client and server reported to
# each other what the maximum chunk sizes were, but that will slow down the
# connection setup with a round trip delay so I'd rather not do that unless it
# is necessary
DEFAULT_MAX_CHUNK = 32 * 1024


def chunkify(msg, max_chunk):
    if len(msg) < max_chunk - 1:
        yield ''.join((msg, "\n"))
    else:
        yield ''.join((json.dumps({
                'chunk-stream': None
            }), "\n"))

        args = [iter(msg)] * (max_chunk - 1)
        for m in map(''.join, itertools.zip_longest(*args, fillvalue='')):
            yield ''.join(itertools.chain(m, "\n"))
        yield "\n"


from .client import AsyncClient, Client
from .serv import AsyncServer, AsyncServerConnection, ClientError, ServerError
