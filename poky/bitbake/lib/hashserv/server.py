# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from contextlib import closing, contextmanager
from datetime import datetime
import asyncio
import logging
import math
import time
from . import create_async_client, TABLE_COLUMNS
import bb.asyncrpc


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


def insert_task(cursor, data, ignore=False):
    keys = sorted(data.keys())
    query = '''INSERT%s INTO tasks_v2 (%s) VALUES (%s)''' % (
        " OR IGNORE" if ignore else "",
        ', '.join(keys),
        ', '.join(':' + k for k in keys))
    cursor.execute(query, data)

async def copy_from_upstream(client, db, method, taskhash):
    d = await client.get_taskhash(method, taskhash, True)
    if d is not None:
        # Filter out unknown columns
        d = {k: v for k, v in d.items() if k in TABLE_COLUMNS}

        with closing(db.cursor()) as cursor:
            insert_task(cursor, d)
            db.commit()

    return d

async def copy_outhash_from_upstream(client, db, method, outhash, taskhash):
    d = await client.get_outhash(method, outhash, taskhash)
    if d is not None:
        # Filter out unknown columns
        d = {k: v for k, v in d.items() if k in TABLE_COLUMNS}

        with closing(db.cursor()) as cursor:
            insert_task(cursor, d)
            db.commit()

    return d

class ServerClient(bb.asyncrpc.AsyncServerConnection):
    FAST_QUERY = 'SELECT taskhash, method, unihash FROM tasks_v2 WHERE method=:method AND taskhash=:taskhash ORDER BY created ASC LIMIT 1'
    ALL_QUERY =  'SELECT *                         FROM tasks_v2 WHERE method=:method AND taskhash=:taskhash ORDER BY created ASC LIMIT 1'
    OUTHASH_QUERY = '''
        -- Find tasks with a matching outhash (that is, tasks that
        -- are equivalent)
        SELECT * FROM tasks_v2 WHERE method=:method AND outhash=:outhash

        -- If there is an exact match on the taskhash, return it.
        -- Otherwise return the oldest matching outhash of any
        -- taskhash
        ORDER BY CASE WHEN taskhash=:taskhash THEN 1 ELSE 2 END,
            created ASC

        -- Only return one row
        LIMIT 1
        '''

    def __init__(self, reader, writer, db, request_stats, backfill_queue, upstream, read_only):
        super().__init__(reader, writer, 'OEHASHEQUIV', logger)
        self.db = db
        self.request_stats = request_stats
        self.max_chunk = bb.asyncrpc.DEFAULT_MAX_CHUNK
        self.backfill_queue = backfill_queue
        self.upstream = upstream

        self.handlers.update({
            'get': self.handle_get,
            'get-outhash': self.handle_get_outhash,
            'get-stream': self.handle_get_stream,
            'get-stats': self.handle_get_stats,
        })

        if not read_only:
            self.handlers.update({
                'report': self.handle_report,
                'report-equiv': self.handle_equivreport,
                'reset-stats': self.handle_reset_stats,
                'backfill-wait': self.handle_backfill_wait,
            })

    def validate_proto_version(self):
        return (self.proto_version > (1, 0) and self.proto_version <= (1, 1))

    async def process_requests(self):
        if self.upstream is not None:
            self.upstream_client = await create_async_client(self.upstream)
        else:
            self.upstream_client = None

        await super().process_requests()

        if self.upstream_client is not None:
            await self.upstream_client.close()

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

        raise bb.asyncrpc.ClientError("Unrecognized command %r" % msg)

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
        elif self.upstream_client is not None:
            d = await copy_from_upstream(self.upstream_client, self.db, method, taskhash)
        else:
            d = None

        self.write_message(d)

    async def handle_get_outhash(self, request):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(self.OUTHASH_QUERY,
                           {k: request[k] for k in ('method', 'outhash', 'taskhash')})

            row = cursor.fetchone()

        if row is not None:
            logger.debug('Found equivalent outhash %s -> %s', (row['outhash'], row['unihash']))
            d = {k: row[k] for k in row.keys()}
        else:
            d = None

        self.write_message(d)

    async def handle_get_stream(self, request):
        self.write_message('ok')

        while True:
            upstream = None

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
                elif self.upstream_client is not None:
                    upstream = await self.upstream_client.get_unihash(method, taskhash)
                    if upstream:
                        msg = ("%s\n" % upstream).encode("utf-8")
                    else:
                        msg = "\n".encode("utf-8")
                else:
                    msg = '\n'.encode('utf-8')

                self.writer.write(msg)
            finally:
                request_measure.end()
                self.request_sample.end()

            await self.writer.drain()

            # Post to the backfill queue after writing the result to minimize
            # the turn around time on a request
            if upstream is not None:
                await self.backfill_queue.put((method, taskhash))

    async def handle_report(self, data):
        with closing(self.db.cursor()) as cursor:
            cursor.execute(self.OUTHASH_QUERY,
                           {k: data[k] for k in ('method', 'outhash', 'taskhash')})

            row = cursor.fetchone()

            if row is None and self.upstream_client:
                # Try upstream
                row = await copy_outhash_from_upstream(self.upstream_client,
                                                       self.db,
                                                       data['method'],
                                                       data['outhash'],
                                                       data['taskhash'])

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

                insert_task(cursor, insert_data)
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

            insert_task(cursor, insert_data, ignore=True)
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

    async def handle_backfill_wait(self, request):
        d = {
            'tasks': self.backfill_queue.qsize(),
        }
        await self.backfill_queue.join()
        self.write_message(d)

    def query_equivalent(self, method, taskhash, query):
        # This is part of the inner loop and must be as fast as possible
        try:
            cursor = self.db.cursor()
            cursor.execute(query, {'method': method, 'taskhash': taskhash})
            return cursor.fetchone()
        except:
            cursor.close()


class Server(bb.asyncrpc.AsyncServer):
    def __init__(self, db, upstream=None, read_only=False):
        if upstream and read_only:
            raise bb.asyncrpc.ServerError("Read-only hashserv cannot pull from an upstream server")

        super().__init__(logger)

        self.request_stats = Stats()
        self.db = db
        self.upstream = upstream
        self.read_only = read_only

    def accept_client(self, reader, writer):
        return ServerClient(reader, writer, self.db, self.request_stats, self.backfill_queue, self.upstream, self.read_only)

    @contextmanager
    def _backfill_worker(self):
        async def backfill_worker_task():
            client = await create_async_client(self.upstream)
            try:
                while True:
                    item = await self.backfill_queue.get()
                    if item is None:
                        self.backfill_queue.task_done()
                        break
                    method, taskhash = item
                    await copy_from_upstream(client, self.db, method, taskhash)
                    self.backfill_queue.task_done()
            finally:
                await client.close()

        async def join_worker(worker):
            await self.backfill_queue.put(None)
            await worker

        if self.upstream is not None:
            worker = asyncio.ensure_future(backfill_worker_task())
            try:
                yield
            finally:
                self.loop.run_until_complete(join_worker(worker))
        else:
            yield

    def run_loop_forever(self):
        self.backfill_queue = asyncio.Queue()

        with self._backfill_worker():
            super().run_loop_forever()
