# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from contextlib import closing, contextmanager
from datetime import datetime
import enum
import asyncio
import logging
import math
import time
from . import create_async_client, UNIHASH_TABLE_COLUMNS, OUTHASH_TABLE_COLUMNS
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


@enum.unique
class Resolve(enum.Enum):
    FAIL = enum.auto()
    IGNORE = enum.auto()
    REPLACE = enum.auto()


def insert_table(cursor, table, data, on_conflict):
    resolve = {
        Resolve.FAIL: "",
        Resolve.IGNORE: " OR IGNORE",
        Resolve.REPLACE: " OR REPLACE",
    }[on_conflict]

    keys = sorted(data.keys())
    query = 'INSERT{resolve} INTO {table} ({fields}) VALUES({values})'.format(
        resolve=resolve,
        table=table,
        fields=", ".join(keys),
        values=", ".join(":" + k for k in keys),
    )
    prevrowid = cursor.lastrowid
    cursor.execute(query, data)
    logging.debug(
        "Inserting %r into %s, %s",
        data,
        table,
        on_conflict
    )
    return (cursor.lastrowid, cursor.lastrowid != prevrowid)

def insert_unihash(cursor, data, on_conflict):
    return insert_table(cursor, "unihashes_v2", data, on_conflict)

def insert_outhash(cursor, data, on_conflict):
    return insert_table(cursor, "outhashes_v2", data, on_conflict)

async def copy_unihash_from_upstream(client, db, method, taskhash):
    d = await client.get_taskhash(method, taskhash)
    if d is not None:
        with closing(db.cursor()) as cursor:
            insert_unihash(
                cursor,
                {k: v for k, v in d.items() if k in UNIHASH_TABLE_COLUMNS},
                Resolve.IGNORE,
            )
            db.commit()
    return d


class ServerCursor(object):
    def __init__(self, db, cursor, upstream):
        self.db = db
        self.cursor = cursor
        self.upstream = upstream


class ServerClient(bb.asyncrpc.AsyncServerConnection):
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
        fetch_all = request.get('all', False)

        with closing(self.db.cursor()) as cursor:
            d = await self.get_unihash(cursor, method, taskhash, fetch_all)

        self.write_message(d)

    async def get_unihash(self, cursor, method, taskhash, fetch_all=False):
        d = None

        if fetch_all:
            cursor.execute(
                '''
                SELECT *, unihashes_v2.unihash AS unihash FROM outhashes_v2
                INNER JOIN unihashes_v2 ON unihashes_v2.method=outhashes_v2.method AND unihashes_v2.taskhash=outhashes_v2.taskhash
                WHERE outhashes_v2.method=:method AND outhashes_v2.taskhash=:taskhash
                ORDER BY outhashes_v2.created ASC
                LIMIT 1
                ''',
                {
                    'method': method,
                    'taskhash': taskhash,
                }

            )
            row = cursor.fetchone()

            if row is not None:
                d = {k: row[k] for k in row.keys()}
            elif self.upstream_client is not None:
                d = await self.upstream_client.get_taskhash(method, taskhash, True)
                self.update_unified(cursor, d)
                self.db.commit()
        else:
            row = self.query_equivalent(cursor, method, taskhash)

            if row is not None:
                d = {k: row[k] for k in row.keys()}
            elif self.upstream_client is not None:
                d = await self.upstream_client.get_taskhash(method, taskhash)
                d = {k: v for k, v in d.items() if k in UNIHASH_TABLE_COLUMNS}
                insert_unihash(cursor, d, Resolve.IGNORE)
                self.db.commit()

        return d

    async def handle_get_outhash(self, request):
        method = request['method']
        outhash = request['outhash']
        taskhash = request['taskhash']

        with closing(self.db.cursor()) as cursor:
            d = await self.get_outhash(cursor, method, outhash, taskhash)

        self.write_message(d)

    async def get_outhash(self, cursor, method, outhash, taskhash):
        d = None
        cursor.execute(
            '''
            SELECT *, unihashes_v2.unihash AS unihash FROM outhashes_v2
            INNER JOIN unihashes_v2 ON unihashes_v2.method=outhashes_v2.method AND unihashes_v2.taskhash=outhashes_v2.taskhash
            WHERE outhashes_v2.method=:method AND outhashes_v2.outhash=:outhash
            ORDER BY outhashes_v2.created ASC
            LIMIT 1
            ''',
            {
                'method': method,
                'outhash': outhash,
            }
        )
        row = cursor.fetchone()

        if row is not None:
            d = {k: row[k] for k in row.keys()}
        elif self.upstream_client is not None:
            d = await self.upstream_client.get_outhash(method, outhash, taskhash)
            self.update_unified(cursor, d)
            self.db.commit()

        return d

    def update_unified(self, cursor, data):
        if data is None:
            return

        insert_unihash(
            cursor,
            {k: v for k, v in data.items() if k in UNIHASH_TABLE_COLUMNS},
            Resolve.IGNORE
        )
        insert_outhash(
            cursor,
            {k: v for k, v in data.items() if k in OUTHASH_TABLE_COLUMNS},
            Resolve.IGNORE
        )

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
                cursor = self.db.cursor()
                try:
                    row = self.query_equivalent(cursor, method, taskhash)
                finally:
                    cursor.close()

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
            outhash_data = {
                'method': data['method'],
                'outhash': data['outhash'],
                'taskhash': data['taskhash'],
                'created': datetime.now()
            }

            for k in ('owner', 'PN', 'PV', 'PR', 'task', 'outhash_siginfo'):
                if k in data:
                    outhash_data[k] = data[k]

            # Insert the new entry, unless it already exists
            (rowid, inserted) = insert_outhash(cursor, outhash_data, Resolve.IGNORE)

            if inserted:
                # If this row is new, check if it is equivalent to another
                # output hash
                cursor.execute(
                    '''
                    SELECT outhashes_v2.taskhash AS taskhash, unihashes_v2.unihash AS unihash FROM outhashes_v2
                    INNER JOIN unihashes_v2 ON unihashes_v2.method=outhashes_v2.method AND unihashes_v2.taskhash=outhashes_v2.taskhash
                    -- Select any matching output hash except the one we just inserted
                    WHERE outhashes_v2.method=:method AND outhashes_v2.outhash=:outhash AND outhashes_v2.taskhash!=:taskhash
                    -- Pick the oldest hash
                    ORDER BY outhashes_v2.created ASC
                    LIMIT 1
                    ''',
                    {
                        'method': data['method'],
                        'outhash': data['outhash'],
                        'taskhash': data['taskhash'],
                    }
                )
                row = cursor.fetchone()

                if row is not None:
                    # A matching output hash was found. Set our taskhash to the
                    # same unihash since they are equivalent
                    unihash = row['unihash']
                    resolve = Resolve.IGNORE
                else:
                    # No matching output hash was found. This is probably the
                    # first outhash to be added.
                    unihash = data['unihash']
                    resolve = Resolve.IGNORE

                    # Query upstream to see if it has a unihash we can use
                    if self.upstream_client is not None:
                        upstream_data = await self.upstream_client.get_outhash(data['method'], data['outhash'], data['taskhash'])
                        if upstream_data is not None:
                            unihash = upstream_data['unihash']


                insert_unihash(
                    cursor,
                    {
                        'method': data['method'],
                        'taskhash': data['taskhash'],
                        'unihash': unihash,
                    },
                    resolve
                )

            unihash_data = await self.get_unihash(cursor, data['method'], data['taskhash'])
            if unihash_data is not None:
                unihash = unihash_data['unihash']
            else:
                unihash = data['unihash']

            self.db.commit()

            d = {
                'taskhash': data['taskhash'],
                'method': data['method'],
                'unihash': unihash,
            }

        self.write_message(d)

    async def handle_equivreport(self, data):
        with closing(self.db.cursor()) as cursor:
            insert_data = {
                'method': data['method'],
                'taskhash': data['taskhash'],
                'unihash': data['unihash'],
            }
            insert_unihash(cursor, insert_data, Resolve.IGNORE)
            self.db.commit()

            # Fetch the unihash that will be reported for the taskhash. If the
            # unihash matches, it means this row was inserted (or the mapping
            # was already valid)
            row = self.query_equivalent(cursor, data['method'], data['taskhash'])

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

    def query_equivalent(self, cursor, method, taskhash):
        # This is part of the inner loop and must be as fast as possible
        cursor.execute(
            'SELECT taskhash, method, unihash FROM unihashes_v2 WHERE method=:method AND taskhash=:taskhash',
            {
                'method': method,
                'taskhash': taskhash,
            }
        )
        return cursor.fetchone()


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
                    await copy_unihash_from_upstream(client, self.db, method, taskhash)
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
