#
# SPDX-License-Identifier: MIT
#

import socket

def get_free_port(udp = False):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM if not udp else socket.SOCK_DGRAM)
    s.bind(('', 0))
    addr = s.getsockname()
    s.close()
    return addr[1]
