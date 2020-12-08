# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

import json
import logging
import socket
import os
from . import chunkify, DEFAULT_MAX_CHUNK


logger = logging.getLogger('hashserv.client')


class HashConnectionError(Exception):
    pass


class Client(object):
    MODE_NORMAL = 0
    MODE_GET_STREAM = 1

    def __init__(self):
        self._socket = None
        self.reader = None
        self.writer = None
        self.mode = self.MODE_NORMAL
        self.max_chunk = DEFAULT_MAX_CHUNK

    def connect_tcp(self, address, port):
        def connect_sock():
            s = socket.create_connection((address, port))

            s.setsockopt(socket.SOL_TCP, socket.TCP_NODELAY, 1)
            s.setsockopt(socket.SOL_TCP, socket.TCP_QUICKACK, 1)
            s.setsockopt(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1)
            return s

        self._connect_sock = connect_sock

    def connect_unix(self, path):
        def connect_sock():
            s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
            # AF_UNIX has path length issues so chdir here to workaround
            cwd = os.getcwd()
            try:
                os.chdir(os.path.dirname(path))
                s.connect(os.path.basename(path))
            finally:
                os.chdir(cwd)
            return s

        self._connect_sock = connect_sock

    def connect(self):
        if self._socket is None:
            self._socket = self._connect_sock()

            self.reader = self._socket.makefile('r', encoding='utf-8')
            self.writer = self._socket.makefile('w', encoding='utf-8')

            self.writer.write('OEHASHEQUIV 1.1\n\n')
            self.writer.flush()

            # Restore mode if the socket is being re-created
            cur_mode = self.mode
            self.mode = self.MODE_NORMAL
            self._set_mode(cur_mode)

        return self._socket

    def close(self):
        if self._socket is not None:
            self._socket.close()
            self._socket = None
            self.reader = None
            self.writer = None

    def _send_wrapper(self, proc):
        count = 0
        while True:
            try:
                self.connect()
                return proc()
            except (OSError, HashConnectionError, json.JSONDecodeError, UnicodeDecodeError) as e:
                logger.warning('Error talking to server: %s' % e)
                if count >= 3:
                    if not isinstance(e, HashConnectionError):
                        raise HashConnectionError(str(e))
                    raise e
                self.close()
                count += 1

    def send_message(self, msg):
        def get_line():
            line = self.reader.readline()
            if not line:
                raise HashConnectionError('Connection closed')

            if not line.endswith('\n'):
                raise HashConnectionError('Bad message %r' % message)

            return line

        def proc():
            for c in chunkify(json.dumps(msg), self.max_chunk):
                self.writer.write(c)
            self.writer.flush()

            l = get_line()

            m = json.loads(l)
            if 'chunk-stream' in m:
                lines = []
                while True:
                    l = get_line().rstrip('\n')
                    if not l:
                        break
                    lines.append(l)

                m = json.loads(''.join(lines))

            return m

        return self._send_wrapper(proc)

    def send_stream(self, msg):
        def proc():
            self.writer.write("%s\n" % msg)
            self.writer.flush()
            l = self.reader.readline()
            if not l:
                raise HashConnectionError('Connection closed')
            return l.rstrip()

        return self._send_wrapper(proc)

    def _set_mode(self, new_mode):
        if new_mode == self.MODE_NORMAL and self.mode == self.MODE_GET_STREAM:
            r = self.send_stream('END')
            if r != 'ok':
                raise HashConnectionError('Bad response from server %r' % r)
        elif new_mode == self.MODE_GET_STREAM and self.mode == self.MODE_NORMAL:
            r = self.send_message({'get-stream': None})
            if r != 'ok':
                raise HashConnectionError('Bad response from server %r' % r)
        elif new_mode != self.mode:
            raise Exception('Undefined mode transition %r -> %r' % (self.mode, new_mode))

        self.mode = new_mode

    def get_unihash(self, method, taskhash):
        self._set_mode(self.MODE_GET_STREAM)
        r = self.send_stream('%s %s' % (method, taskhash))
        if not r:
            return None
        return r

    def report_unihash(self, taskhash, method, outhash, unihash, extra={}):
        self._set_mode(self.MODE_NORMAL)
        m = extra.copy()
        m['taskhash'] = taskhash
        m['method'] = method
        m['outhash'] = outhash
        m['unihash'] = unihash
        return self.send_message({'report': m})

    def report_unihash_equiv(self, taskhash, method, unihash, extra={}):
        self._set_mode(self.MODE_NORMAL)
        m = extra.copy()
        m['taskhash'] = taskhash
        m['method'] = method
        m['unihash'] = unihash
        return self.send_message({'report-equiv': m})

    def get_taskhash(self, method, taskhash, all_properties=False):
        self._set_mode(self.MODE_NORMAL)
        return self.send_message({'get': {
            'taskhash': taskhash,
            'method': method,
            'all': all_properties
        }})

    def get_stats(self):
        self._set_mode(self.MODE_NORMAL)
        return self.send_message({'get-stats': None})

    def reset_stats(self):
        self._set_mode(self.MODE_NORMAL)
        return self.send_message({'reset-stats': None})
