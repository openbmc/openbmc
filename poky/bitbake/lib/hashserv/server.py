# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from contextlib import closing
from datetime import datetime
import asyncio
import json
import logging
import math
import os
import signal
import socket
import time
from . import chunkify, DEFAULT_MAX_CHUNK

logger = logging.getLogger('hashserv.server')


class Measurement(object):
    def __init__(self, sample):
        self.sample = sample

    def start(self):
        self.start_time = time.perf_counter()

    def end(self):
        self.sample.add(time.perf_counter() - self.start_time)

    def __enter__(self):
        self.start()
        return self

    def __exit__(self, *args, **kwargs):
        self.end()


class Sample(object):
    def __init__(self, stats):
        self.stats = stats
        self.num_samples = 0
        self.elapsed = 0

    def measure(self):
        return Measurement(self)

    def __enter__(self):
        return self

    def __exit__(self, *args, **kwargs):
        self.end()

    def add(self, elapsed):
        self.num_samples += 1
        self.elapsed += elapsed

    def end(self):
        if self.num_samples:
            self.stats.add(self.elapsed)
            self.num_samples = 0
            self.elapsed = 0


class Stats(object):
    def __init__(self):
        self.reset()

    def reset(self):
        self.num = 0
        self.total_time = 0
        self.max_time = 0
        self.m = 0
        self.s = 0
        self.current_elapsed = None

    def add(self, elapsed):
        self.num += 1
        if self.num == 1:
            self.m = elapsed
            self.s = 0
        else:
            last_m = self.m
            self.m = last_m + (elapsed - last_m) / self.num
            self.s = self.s + (elapsed - last_m) * (elapsed - self.m)

        self.total_time += elapsed

        if self.max_time < elapsed:
            self.max_time = elapsed

    def start_sample(self):
        return Sample(self)

    @property
    def average(self):
        if self.num == 0:
            return 0
        return self.total_time / self.num

    @property
    def stdev(self):
        if self.num <= 1:
            return 0
        return math.sqrt(self.s / (self.num - 1))

    def todict(self):
        return {k: getattr(self, k) for k in ('num', 'total_time', 'max_time', 'average', 'stdev')}


class ClientError(Exception):
    pass

class ServerClient(object):
    FAST_QUERY = 'SELECT taskhash, method, unihash FROM tasks_v2 WHERE method=:method AND taskhash=:taskhash ORDER BY created ASC LIMIT 1'
    ALL_QUERY =  'SELECT *                         FROM tasks_v2 WHERE method=:method AND taskhash=:taskhash ORDER BY created ASC LIMIT 1'

    def __init__(self, reader, writer, db, request_stats):
        self.reader = reader
        self.writer = writer
        self.db = db
        self.request_stats = request_stats
        self.max_chunk = DEFAULT_MAX_CHUNK

        self.handlers = {
            'get': self.handle_get,
            'report': self.handle_report,
            'report-equiv': self.handle_equivreport,
            'get-stream': self.handle_get_stream,
            'get-stats': self.handle_get_stats,
            'reset-stats': self.handle_reset_stats,
            'chunk-stream': self.handle_chunk,
        }

    async def process_requests(self):
        try:
            self.addr = self.writer.get_extra_info('peername')
            logger.debug('Client %r connected' % (self.addr,))

            # Read protocol and version
            protocol = await self.reader.readline()
            if protocol is None:
                return

            (proto_name, proto_version) = protocol.decode('utf-8').rstrip().split()
            if proto_name != 'OEHASHEQUIV':
                return

            proto_version = tuple(int(v) for v in proto_version.split('.'))
            if proto_version < (1, 0) or proto_version > (1, 1):
                return

            # Read headers. Currently, no headers are implemented, so look for
            # an empty line to signal the end of the headers
            while True:
                line = await self.reader.readline()
                if line is None:
                    return

                line = line.decode('utf-8').rstrip()
                if not line:
                    break

            # Handle messages
            while True:
                d = await self.read_message()
                if d is None:
                    break
                await self.dispatch_message(d)
                await self.writer.drain()
        except ClientError as e:
            logger.error(str(e))
        finally:
            self.writer.close()

    async def dispatch_message(self, msg):
        for k in self.handlers.keys():
            if k in msg:
                logger.debug('Handling %s' % k)
                if 'stream' in k:
                    await self.handlers[k](msg[k])
                else:
                    with self.request_stats.start_sample() as self.request_sample, \
                            self.request_sample.measure():
                        await self.handlers[k](msg[k])
                return

        raise ClientError("Unrecognized command %r" % msg)

    def write_message(self, msg):
        for c in chunkify(json.dumps(msg), self.max_chunk):
            self.writer.write(c.encode('utf-8'))

    async def read_message(self):
        l = await self.reader.readline()
        if not l:
            return None

        try:
            message = l.decode('utf-8')

            if not message.endswith('\n'):
                return None

            return json.loads(message)
        except (json.JSONDecodeError, UnicodeDecodeError) as e:
            logger.error('Bad message from client: %r' % message)
            raise e

    async def handle_chunk(self, request):
        lines = []
        try:
            while True:
                l = await self.reader.readline()
                l = l.rstrip(b"\n").decode("utf-8")
                if not l:
                    break
                lines.append(l)

            msg = json.loads(''.join(lines))
        except (json.JSONDecodeError, UnicodeDecodeError) as e:
            logger.error('Bad message from client: %r' % message)
            raise e

        if 'chunk-stream' in msg:
            raise ClientError("Nested chunks are not allowed")

        await self.dispatch_message(msg)

    async def handle_get(self, request):
        method = request['method']
        taskhash = request['taskhash']

        if request.get('all', False):
            row = self.query_equivalent(method, taskhash, self.ALL_QUERY)
        else:
            row = self.query_equivalent(method, taskhash, self.FAST_QUERY)

        if row is not None:
            logger.debug('Found equivalent task %s -> %s', (row['taskhash'], row['unihash']))
            d = {k: row[k] for k in row.keys()}

            self.write_message(d)
        else:
            self.write_message(None)

    async def handle_get_stream(self, request):
        self.write_message('ok')

        while True:
            l = await self.reader.readline()
            if not l:
                return

            try:
                # This inner loop is very sensitive and must be as fast as
                # possible (which is why the request sample is handled manually
                # instead of using 'with', and also why logging statements are
                # commented out.
                self.request_sample = self.request_stats.start_sample()
                request_measure = self.request_sample.measure()
                request_measure.start()

                l = l.decode('utf-8').rstrip()
                if l == 'END':
                    self.writer.write('ok\n'.encode('utf-8'))
                    return

                (method, taskhash) = l.split()
                #logger.debug('Looking up %s %s' % (method, taskhash))
                row = self.query_equivalent(method, taskhash, self.FAST_QUERY)
                if row is not None:
                    msg = ('%s\n' % row['unihash']).encode('utf-8')
                    #logger.debug('Found equivalent task %s -> %s', (row['taskhash'], row['unihash']))
                else:
                    msg = '\n'.encode('utf-8')

                self.writer.write(msg)
            finally:
                request_measure.end()
                self.request_sample.end()

            await self.writer.drain()

    async def handle_report(self, data):
        with closing(self.db.cursor()) as cursor:
            cursor.execute('''
                -- Find tasks with a matching outhash (that is, tasks that
                -- are equivalent)
                SELECT taskhash, method, unihash FROM tasks_v2 WHERE method=:method AND outhash=:outhash

                -- If there is an exact match on the taskhash, return it.
                -- Otherwise return the oldest matching outhash of any
                -- taskhash
                ORDER BY CASE WHEN taskhash=:taskhash THEN 1 ELSE 2 END,
                    created ASC

                -- Only return one row
                LIMIT 1
                ''', {k: data[k] for k in ('method', 'outhash', 'taskhash')})

            row = cursor.fetchone()

            # If no matching outhash was found, or one *was* found but it
            # wasn't an exact match on the taskhash, a new entry for this
            # taskhash should be added
            if row is None or row['taskhash'] != data['taskhash']:
                # If a row matching the outhash was found, the unihash for
                # the new taskhash should be the same as that one.
                # Otherwise the caller provided unihash is used.
                unihash = data['unihash']
                if row is not None:
                    unihash = row['unihash']

                insert_data = {
                    'method': data['method'],
                    'outhash': data['outhash'],
                    'taskhash': data['taskhash'],
                    'unihash': unihash,
                    'created': datetime.now()
                }

                for k in ('owner', 'PN', 'PV', 'PR', 'task', 'outhash_siginfo'):
                    if k in data:
                        insert_data[k] = data[k]

                cursor.execute('''INSERT INTO tasks_v2 (%s) VALUES (%s)''' % (
                    ', '.join(sorted(insert_data.keys())),
                    ', '.join(':' + k for k in sorted(insert_data.keys()))),
                    insert_data)

                self.db.commit()

                logger.info('Adding taskhash %s with unihash %s',
                            data['taskhash'], unihash)

                d = {
                    'taskhash': data['taskhash'],
                    'method': data['method'],
                    'unihash': unihash
                }
            else:
                d = {k: row[k] for k in ('taskhash', 'method', 'unihash')}

        self.write_message(d)

    async def handle_equivreport(self, data):
        with closing(self.db.cursor()) as cursor:
            insert_data = {
                'method': data['method'],
                'outhash': "",
                'taskhash': data['taskhash'],
                'unihash': data['unihash'],
                'created': datetime.now()
            }

            for k in ('owner', 'PN', 'PV', 'PR', 'task', 'outhash_siginfo'):
                if k in data:
                    insert_data[k] = data[k]

            cursor.execute('''INSERT OR IGNORE INTO tasks_v2 (%s) VALUES (%s)''' % (
                ', '.join(sorted(insert_data.keys())),
                ', '.join(':' + k for k in sorted(insert_data.keys()))),
                insert_data)

            self.db.commit()

            # Fetch the unihash that will be reported for the taskhash. If the
            # unihash matches, it means this row was inserted (or the mapping
            # was already valid)
            row = self.query_equivalent(data['method'], data['taskhash'], self.FAST_QUERY)

            if row['unihash'] == data['unihash']:
                logger.info('Adding taskhash equivalence for %s with unihash %s',
                                data['taskhash'], row['unihash'])

            d = {k: row[k] for k in ('taskhash', 'method', 'unihash')}

        self.write_message(d)


    async def handle_get_stats(self, request):
        d = {
            'requests': self.request_stats.todict(),
        }

        self.write_message(d)

    async def handle_reset_stats(self, request):
        d = {
            'requests': self.request_stats.todict(),
        }

        self.request_stats.reset()
        self.write_message(d)

    def query_equivalent(self, method, taskhash, query):
        # This is part of the inner loop and must be as fast as possible
        try:
            cursor = self.db.cursor()
            cursor.execute(query, {'method': method, 'taskhash': taskhash})
            return cursor.fetchone()
        except:
            cursor.close()


class Server(object):
    def __init__(self, db, loop=None):
        self.request_stats = Stats()
        self.db = db

        if loop is None:
            self.loop = asyncio.new_event_loop()
            self.close_loop = True
        else:
            self.loop = loop
            self.close_loop = False

        self._cleanup_socket = None

    def start_tcp_server(self, host, port):
        self.server = self.loop.run_until_complete(
            asyncio.start_server(self.handle_client, host, port, loop=self.loop)
        )

        for s in self.server.sockets:
            logger.info('Listening on %r' % (s.getsockname(),))
            # Newer python does this automatically. Do it manually here for
            # maximum compatibility
            s.setsockopt(socket.SOL_TCP, socket.TCP_NODELAY, 1)
            s.setsockopt(socket.SOL_TCP, socket.TCP_QUICKACK, 1)

        name = self.server.sockets[0].getsockname()
        if self.server.sockets[0].family == socket.AF_INET6:
            self.address = "[%s]:%d" % (name[0], name[1])
        else:
            self.address = "%s:%d" % (name[0], name[1])

    def start_unix_server(self, path):
        def cleanup():
            os.unlink(path)

        cwd = os.getcwd()
        try:
            # Work around path length limits in AF_UNIX
            os.chdir(os.path.dirname(path))
            self.server = self.loop.run_until_complete(
                asyncio.start_unix_server(self.handle_client, os.path.basename(path), loop=self.loop)
            )
        finally:
            os.chdir(cwd)

        logger.info('Listening on %r' % path)

        self._cleanup_socket = cleanup
        self.address = "unix://%s" % os.path.abspath(path)

    async def handle_client(self, reader, writer):
        # writer.transport.set_write_buffer_limits(0)
        try:
            client = ServerClient(reader, writer, self.db, self.request_stats)
            await client.process_requests()
        except Exception as e:
            import traceback
            logger.error('Error from client: %s' % str(e), exc_info=True)
            traceback.print_exc()
            writer.close()
        logger.info('Client disconnected')

    def serve_forever(self):
        def signal_handler():
            self.loop.stop()

        self.loop.add_signal_handler(signal.SIGTERM, signal_handler)

        try:
            self.loop.run_forever()
        except KeyboardInterrupt:
            pass

        self.server.close()
        self.loop.run_until_complete(self.server.wait_closed())
        logger.info('Server shutting down')

        if self.close_loop:
            self.loop.close()

        if self._cleanup_socket is not None:
            self._cleanup_socket()
