#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#


__version__ = "2.0.0"

import logging
logger = logging.getLogger("BitBake.PRserv")

from bb.asyncrpc.client import parse_address, ADDR_TYPE_UNIX, ADDR_TYPE_WS

def create_server(addr, dbpath, upstream=None, read_only=False):
    from . import serv

    s = serv.PRServer(dbpath, upstream=upstream, read_only=read_only)
    host, port = addr.split(":")
    s.start_tcp_server(host, int(port))

    return s

def increase_revision(ver):
    """Take a revision string such as "1" or "1.2.3" or even a number and increase its last number
    This fails if the last number is not an integer"""

    fields=str(ver).split('.')
    last = fields[-1]

    try:
         val = int(last)
    except Exception as e:
         logger.critical("Unable to increase revision value %s: %s" % (ver, e))
         raise e

    return ".".join(fields[0:-1] + [ str(val + 1) ])

def _revision_greater_or_equal(rev1, rev2):
    """Compares x.y.z revision numbers, using integer comparison
    Returns True if rev1 is greater or equal to rev2"""

    fields1 = rev1.split(".")
    fields2 = rev2.split(".")
    l1 = len(fields1)
    l2 = len(fields2)

    for i in range(l1):
       val1 = int(fields1[i])
       if i < l2:
           val2 = int(fields2[i])
           if val2 < val1:
              return True
           elif val2 > val1:
              return False
       else:
          return True
    return True

def revision_smaller(rev1, rev2):
    """Compares x.y.z revision numbers, using integer comparison
    Returns True if rev1 is strictly smaller than rev2"""
    return not(_revision_greater_or_equal(rev1, rev2))

def revision_greater(rev1, rev2):
    """Compares x.y.z revision numbers, using integer comparison
    Returns True if rev1 is strictly greater than rev2"""
    return _revision_greater_or_equal(rev1, rev2) and (rev1 != rev2)

def create_client(addr):
    from . import client

    c = client.PRClient()

    try:
        (typ, a) = parse_address(addr)
        c.connect_tcp(*a)
        return c
    except Exception as e:
        c.close()
        raise e

async def create_async_client(addr):
    from . import client

    c = client.PRAsyncClient()

    try:
        (typ, a) = parse_address(addr)
        await c.connect_tcp(*a)
        return c

    except Exception as e:
        await c.close()
        raise e
