# Copyright (C) 2019 Garmin Ltd.
#
# SPDX-License-Identifier: GPL-2.0-only
#

from datetime import datetime, timedelta
import asyncio
import logging
import math
import time
import os
import base64
import hashlib
from . import create_async_client
import bb.asyncrpc

logger = logging.getLogger("hashserv.server")


# This permission only exists to match nothing
NONE_PERM = "@none"

READ_PERM = "@read"
REPORT_PERM = "@report"
DB_ADMIN_PERM = "@db-admin"
USER_ADMIN_PERM = "@user-admin"
ALL_PERM = "@all"

ALL_PERMISSIONS = {
    READ_PERM,
    REPORT_PERM,
    DB_ADMIN_PERM,
    USER_ADMIN_PERM,
    ALL_PERM,
}

DEFAULT_ANON_PERMS = (
    READ_PERM,
    REPORT_PERM,
    DB_ADMIN_PERM,
)

TOKEN_ALGORITHM = "sha256"

# 48 bytes of random data will result in 64 characters when base64
# encoded. This number also ensures that the base64 encoding won't have any
# trailing '=' characters.
TOKEN_SIZE = 48

SALT_SIZE = 8


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
        return {
            k: getattr(self, k)
            for k in ("num", "total_time", "max_time", "average", "stdev")
        }


token_refresh_semaphore = asyncio.Lock()


async def new_token():
    # Prevent malicious users from using this API to deduce the entropy
    # pool on the server and thus be able to guess a token. *All* token
    # refresh requests lock the same global semaphore and then sleep for a
    # short time. The effectively rate limits the total number of requests
    # than can be made across all clients to 10/second, which should be enough
    # since you have to be an authenticated users to make the request in the
    # first place
    async with token_refresh_semaphore:
        await asyncio.sleep(0.1)
        raw = os.getrandom(TOKEN_SIZE, os.GRND_NONBLOCK)

    return base64.b64encode(raw, b"._").decode("utf-8")


def new_salt():
    return os.getrandom(SALT_SIZE, os.GRND_NONBLOCK).hex()


def hash_token(algo, salt, token):
    h = hashlib.new(algo)
    h.update(salt.encode("utf-8"))
    h.update(token.encode("utf-8"))
    return ":".join([algo, salt, h.hexdigest()])


def permissions(*permissions, allow_anon=True, allow_self_service=False):
    """
    Function decorator that can be used to decorate an RPC function call and
    check that the current users permissions match the require permissions.

    If allow_anon is True, the user will also be allowed to make the RPC call
    if the anonymous user permissions match the permissions.

    If allow_self_service is True, and the "username" property in the request
    is the currently logged in user, or not specified, the user will also be
    allowed to make the request. This allows users to access normal privileged
    API, as long as they are only modifying their own user properties (e.g.
    users can be allowed to reset their own token without @user-admin
    permissions, but not the token for any other user.
    """

    def wrapper(func):
        async def wrap(self, request):
            if allow_self_service and self.user is not None:
                username = request.get("username", self.user.username)
                if username == self.user.username:
                    request["username"] = self.user.username
                    return await func(self, request)

            if not self.user_has_permissions(*permissions, allow_anon=allow_anon):
                if not self.user:
                    username = "Anonymous user"
                    user_perms = self.server.anon_perms
                else:
                    username = self.user.username
                    user_perms = self.user.permissions

                self.logger.info(
                    "User %s with permissions %r denied from calling %s. Missing permissions(s) %r",
                    username,
                    ", ".join(user_perms),
                    func.__name__,
                    ", ".join(permissions),
                )
                raise bb.asyncrpc.InvokeError(
                    f"{username} is not allowed to access permissions(s) {', '.join(permissions)}"
                )

            return await func(self, request)

        return wrap

    return wrapper


class ServerClient(bb.asyncrpc.AsyncServerConnection):
    def __init__(self, socket, server):
        super().__init__(socket, "OEHASHEQUIV", server.logger)
        self.server = server
        self.max_chunk = bb.asyncrpc.DEFAULT_MAX_CHUNK
        self.user = None

        self.handlers.update(
            {
                "get": self.handle_get,
                "get-outhash": self.handle_get_outhash,
                "get-stream": self.handle_get_stream,
                "exists-stream": self.handle_exists_stream,
                "get-stats": self.handle_get_stats,
                "get-db-usage": self.handle_get_db_usage,
                "get-db-query-columns": self.handle_get_db_query_columns,
                # Not always read-only, but internally checks if the server is
                # read-only
                "report": self.handle_report,
                "auth": self.handle_auth,
                "get-user": self.handle_get_user,
                "get-all-users": self.handle_get_all_users,
                "become-user": self.handle_become_user,
            }
        )

        if not self.server.read_only:
            self.handlers.update(
                {
                    "report-equiv": self.handle_equivreport,
                    "reset-stats": self.handle_reset_stats,
                    "backfill-wait": self.handle_backfill_wait,
                    "remove": self.handle_remove,
                    "gc-mark": self.handle_gc_mark,
                    "gc-sweep": self.handle_gc_sweep,
                    "gc-status": self.handle_gc_status,
                    "clean-unused": self.handle_clean_unused,
                    "refresh-token": self.handle_refresh_token,
                    "set-user-perms": self.handle_set_perms,
                    "new-user": self.handle_new_user,
                    "delete-user": self.handle_delete_user,
                }
            )

    def raise_no_user_error(self, username):
        raise bb.asyncrpc.InvokeError(f"No user named '{username}' exists")

    def user_has_permissions(self, *permissions, allow_anon=True):
        permissions = set(permissions)
        if allow_anon:
            if ALL_PERM in self.server.anon_perms:
                return True

            if not permissions - self.server.anon_perms:
                return True

        if self.user is None:
            return False

        if ALL_PERM in self.user.permissions:
            return True

        if not permissions - self.user.permissions:
            return True

        return False

    def validate_proto_version(self):
        return self.proto_version > (1, 0) and self.proto_version <= (1, 1)

    async def process_requests(self):
        async with self.server.db_engine.connect(self.logger) as db:
            self.db = db
            if self.server.upstream is not None:
                self.upstream_client = await create_async_client(self.server.upstream)
            else:
                self.upstream_client = None

            try:
                await super().process_requests()
            finally:
                if self.upstream_client is not None:
                    await self.upstream_client.close()

    async def dispatch_message(self, msg):
        for k in self.handlers.keys():
            if k in msg:
                self.logger.debug("Handling %s" % k)
                if "stream" in k:
                    return await self.handlers[k](msg[k])
                else:
                    with self.server.request_stats.start_sample() as self.request_sample, self.request_sample.measure():
                        return await self.handlers[k](msg[k])

        raise bb.asyncrpc.ClientError("Unrecognized command %r" % msg)

    @permissions(READ_PERM)
    async def handle_get(self, request):
        method = request["method"]
        taskhash = request["taskhash"]
        fetch_all = request.get("all", False)

        return await self.get_unihash(method, taskhash, fetch_all)

    async def get_unihash(self, method, taskhash, fetch_all=False):
        d = None

        if fetch_all:
            row = await self.db.get_unihash_by_taskhash_full(method, taskhash)
            if row is not None:
                d = {k: row[k] for k in row.keys()}
            elif self.upstream_client is not None:
                d = await self.upstream_client.get_taskhash(method, taskhash, True)
                await self.update_unified(d)
        else:
            row = await self.db.get_equivalent(method, taskhash)

            if row is not None:
                d = {k: row[k] for k in row.keys()}
            elif self.upstream_client is not None:
                d = await self.upstream_client.get_taskhash(method, taskhash)
                await self.db.insert_unihash(d["method"], d["taskhash"], d["unihash"])

        return d

    @permissions(READ_PERM)
    async def handle_get_outhash(self, request):
        method = request["method"]
        outhash = request["outhash"]
        taskhash = request["taskhash"]
        with_unihash = request.get("with_unihash", True)

        return await self.get_outhash(method, outhash, taskhash, with_unihash)

    async def get_outhash(self, method, outhash, taskhash, with_unihash=True):
        d = None
        if with_unihash:
            row = await self.db.get_unihash_by_outhash(method, outhash)
        else:
            row = await self.db.get_outhash(method, outhash)

        if row is not None:
            d = {k: row[k] for k in row.keys()}
        elif self.upstream_client is not None:
            d = await self.upstream_client.get_outhash(method, outhash, taskhash)
            await self.update_unified(d)

        return d

    async def update_unified(self, data):
        if data is None:
            return

        await self.db.insert_unihash(data["method"], data["taskhash"], data["unihash"])
        await self.db.insert_outhash(data)

    async def _stream_handler(self, handler):
        await self.socket.send_message("ok")

        while True:
            upstream = None

            l = await self.socket.recv()
            if not l:
                break

            try:
                # This inner loop is very sensitive and must be as fast as
                # possible (which is why the request sample is handled manually
                # instead of using 'with', and also why logging statements are
                # commented out.
                self.request_sample = self.server.request_stats.start_sample()
                request_measure = self.request_sample.measure()
                request_measure.start()

                if l == "END":
                    break

                msg = await handler(l)
                await self.socket.send(msg)
            finally:
                request_measure.end()
                self.request_sample.end()

        await self.socket.send("ok")
        return self.NO_RESPONSE

    @permissions(READ_PERM)
    async def handle_get_stream(self, request):
        async def handler(l):
            (method, taskhash) = l.split()
            # self.logger.debug('Looking up %s %s' % (method, taskhash))
            row = await self.db.get_equivalent(method, taskhash)

            if row is not None:
                # self.logger.debug('Found equivalent task %s -> %s', (row['taskhash'], row['unihash']))
                return row["unihash"]

            if self.upstream_client is not None:
                upstream = await self.upstream_client.get_unihash(method, taskhash)
                if upstream:
                    await self.server.backfill_queue.put((method, taskhash))
                    return upstream

            return ""

        return await self._stream_handler(handler)

    @permissions(READ_PERM)
    async def handle_exists_stream(self, request):
        async def handler(l):
            if await self.db.unihash_exists(l):
                return "true"

            if self.upstream_client is not None:
                if await self.upstream_client.unihash_exists(l):
                    return "true"

            return "false"

        return await self._stream_handler(handler)

    async def report_readonly(self, data):
        method = data["method"]
        outhash = data["outhash"]
        taskhash = data["taskhash"]

        info = await self.get_outhash(method, outhash, taskhash)
        if info:
            unihash = info["unihash"]
        else:
            unihash = data["unihash"]

        return {
            "taskhash": taskhash,
            "method": method,
            "unihash": unihash,
        }

    # Since this can be called either read only or to report, the check to
    # report is made inside the function
    @permissions(READ_PERM)
    async def handle_report(self, data):
        if self.server.read_only or not self.user_has_permissions(REPORT_PERM):
            return await self.report_readonly(data)

        outhash_data = {
            "method": data["method"],
            "outhash": data["outhash"],
            "taskhash": data["taskhash"],
            "created": datetime.now(),
        }

        for k in ("owner", "PN", "PV", "PR", "task", "outhash_siginfo"):
            if k in data:
                outhash_data[k] = data[k]

        if self.user:
            outhash_data["owner"] = self.user.username

        # Insert the new entry, unless it already exists
        if await self.db.insert_outhash(outhash_data):
            # If this row is new, check if it is equivalent to another
            # output hash
            row = await self.db.get_equivalent_for_outhash(
                data["method"], data["outhash"], data["taskhash"]
            )

            if row is not None:
                # A matching output hash was found. Set our taskhash to the
                # same unihash since they are equivalent
                unihash = row["unihash"]
            else:
                # No matching output hash was found. This is probably the
                # first outhash to be added.
                unihash = data["unihash"]

                # Query upstream to see if it has a unihash we can use
                if self.upstream_client is not None:
                    upstream_data = await self.upstream_client.get_outhash(
                        data["method"], data["outhash"], data["taskhash"]
                    )
                    if upstream_data is not None:
                        unihash = upstream_data["unihash"]

            await self.db.insert_unihash(data["method"], data["taskhash"], unihash)

        unihash_data = await self.get_unihash(data["method"], data["taskhash"])
        if unihash_data is not None:
            unihash = unihash_data["unihash"]
        else:
            unihash = data["unihash"]

        return {
            "taskhash": data["taskhash"],
            "method": data["method"],
            "unihash": unihash,
        }

    @permissions(READ_PERM, REPORT_PERM)
    async def handle_equivreport(self, data):
        await self.db.insert_unihash(data["method"], data["taskhash"], data["unihash"])

        # Fetch the unihash that will be reported for the taskhash. If the
        # unihash matches, it means this row was inserted (or the mapping
        # was already valid)
        row = await self.db.get_equivalent(data["method"], data["taskhash"])

        if row["unihash"] == data["unihash"]:
            self.logger.info(
                "Adding taskhash equivalence for %s with unihash %s",
                data["taskhash"],
                row["unihash"],
            )

        return {k: row[k] for k in ("taskhash", "method", "unihash")}

    @permissions(READ_PERM)
    async def handle_get_stats(self, request):
        return {
            "requests": self.server.request_stats.todict(),
        }

    @permissions(DB_ADMIN_PERM)
    async def handle_reset_stats(self, request):
        d = {
            "requests": self.server.request_stats.todict(),
        }

        self.server.request_stats.reset()
        return d

    @permissions(READ_PERM)
    async def handle_backfill_wait(self, request):
        d = {
            "tasks": self.server.backfill_queue.qsize(),
        }
        await self.server.backfill_queue.join()
        return d

    @permissions(DB_ADMIN_PERM)
    async def handle_remove(self, request):
        condition = request["where"]
        if not isinstance(condition, dict):
            raise TypeError("Bad condition type %s" % type(condition))

        return {"count": await self.db.remove(condition)}

    @permissions(DB_ADMIN_PERM)
    async def handle_gc_mark(self, request):
        condition = request["where"]
        mark = request["mark"]

        if not isinstance(condition, dict):
            raise TypeError("Bad condition type %s" % type(condition))

        if not isinstance(mark, str):
            raise TypeError("Bad mark type %s" % type(mark))

        return {"count": await self.db.gc_mark(mark, condition)}

    @permissions(DB_ADMIN_PERM)
    async def handle_gc_sweep(self, request):
        mark = request["mark"]

        if not isinstance(mark, str):
            raise TypeError("Bad mark type %s" % type(mark))

        current_mark = await self.db.get_current_gc_mark()

        if not current_mark or mark != current_mark:
            raise bb.asyncrpc.InvokeError(
                f"'{mark}' is not the current mark. Refusing to sweep"
            )

        count = await self.db.gc_sweep()

        return {"count": count}

    @permissions(DB_ADMIN_PERM)
    async def handle_gc_status(self, request):
        (keep_rows, remove_rows, current_mark) = await self.db.gc_status()
        return {
            "keep": keep_rows,
            "remove": remove_rows,
            "mark": current_mark,
        }

    @permissions(DB_ADMIN_PERM)
    async def handle_clean_unused(self, request):
        max_age = request["max_age_seconds"]
        oldest = datetime.now() - timedelta(seconds=-max_age)
        return {"count": await self.db.clean_unused(oldest)}

    @permissions(DB_ADMIN_PERM)
    async def handle_get_db_usage(self, request):
        return {"usage": await self.db.get_usage()}

    @permissions(DB_ADMIN_PERM)
    async def handle_get_db_query_columns(self, request):
        return {"columns": await self.db.get_query_columns()}

    # The authentication API is always allowed
    async def handle_auth(self, request):
        username = str(request["username"])
        token = str(request["token"])

        async def fail_auth():
            nonlocal username
            # Rate limit bad login attempts
            await asyncio.sleep(1)
            raise bb.asyncrpc.InvokeError(f"Unable to authenticate as {username}")

        user, db_token = await self.db.lookup_user_token(username)

        if not user or not db_token:
            await fail_auth()

        try:
            algo, salt, _ = db_token.split(":")
        except ValueError:
            await fail_auth()

        if hash_token(algo, salt, token) != db_token:
            await fail_auth()

        self.user = user

        self.logger.info("Authenticated as %s", username)

        return {
            "result": True,
            "username": self.user.username,
            "permissions": sorted(list(self.user.permissions)),
        }

    @permissions(USER_ADMIN_PERM, allow_self_service=True, allow_anon=False)
    async def handle_refresh_token(self, request):
        username = str(request["username"])

        token = await new_token()

        updated = await self.db.set_user_token(
            username,
            hash_token(TOKEN_ALGORITHM, new_salt(), token),
        )
        if not updated:
            self.raise_no_user_error(username)

        return {"username": username, "token": token}

    def get_perm_arg(self, arg):
        if not isinstance(arg, list):
            raise bb.asyncrpc.InvokeError("Unexpected type for permissions")

        arg = set(arg)
        try:
            arg.remove(NONE_PERM)
        except KeyError:
            pass

        unknown_perms = arg - ALL_PERMISSIONS
        if unknown_perms:
            raise bb.asyncrpc.InvokeError(
                "Unknown permissions %s" % ", ".join(sorted(list(unknown_perms)))
            )

        return sorted(list(arg))

    def return_perms(self, permissions):
        if ALL_PERM in permissions:
            return sorted(list(ALL_PERMISSIONS))
        return sorted(list(permissions))

    @permissions(USER_ADMIN_PERM, allow_anon=False)
    async def handle_set_perms(self, request):
        username = str(request["username"])
        permissions = self.get_perm_arg(request["permissions"])

        if not await self.db.set_user_perms(username, permissions):
            self.raise_no_user_error(username)

        return {
            "username": username,
            "permissions": self.return_perms(permissions),
        }

    @permissions(USER_ADMIN_PERM, allow_self_service=True, allow_anon=False)
    async def handle_get_user(self, request):
        username = str(request["username"])

        user = await self.db.lookup_user(username)
        if user is None:
            return None

        return {
            "username": user.username,
            "permissions": self.return_perms(user.permissions),
        }

    @permissions(USER_ADMIN_PERM, allow_anon=False)
    async def handle_get_all_users(self, request):
        users = await self.db.get_all_users()
        return {
            "users": [
                {
                    "username": u.username,
                    "permissions": self.return_perms(u.permissions),
                }
                for u in users
            ]
        }

    @permissions(USER_ADMIN_PERM, allow_anon=False)
    async def handle_new_user(self, request):
        username = str(request["username"])
        permissions = self.get_perm_arg(request["permissions"])

        token = await new_token()

        inserted = await self.db.new_user(
            username,
            permissions,
            hash_token(TOKEN_ALGORITHM, new_salt(), token),
        )
        if not inserted:
            raise bb.asyncrpc.InvokeError(f"Cannot create new user '{username}'")

        return {
            "username": username,
            "permissions": self.return_perms(permissions),
            "token": token,
        }

    @permissions(USER_ADMIN_PERM, allow_self_service=True, allow_anon=False)
    async def handle_delete_user(self, request):
        username = str(request["username"])

        if not await self.db.delete_user(username):
            self.raise_no_user_error(username)

        return {"username": username}

    @permissions(USER_ADMIN_PERM, allow_anon=False)
    async def handle_become_user(self, request):
        username = str(request["username"])

        user = await self.db.lookup_user(username)
        if user is None:
            raise bb.asyncrpc.InvokeError(f"User {username} doesn't exist")

        self.user = user

        self.logger.info("Became user %s", username)

        return {
            "username": self.user.username,
            "permissions": self.return_perms(self.user.permissions),
        }


class Server(bb.asyncrpc.AsyncServer):
    def __init__(
        self,
        db_engine,
        upstream=None,
        read_only=False,
        anon_perms=DEFAULT_ANON_PERMS,
        admin_username=None,
        admin_password=None,
    ):
        if upstream and read_only:
            raise bb.asyncrpc.ServerError(
                "Read-only hashserv cannot pull from an upstream server"
            )

        disallowed_perms = set(anon_perms) - set(
            [NONE_PERM, READ_PERM, REPORT_PERM, DB_ADMIN_PERM]
        )

        if disallowed_perms:
            raise bb.asyncrpc.ServerError(
                f"Permission(s) {' '.join(disallowed_perms)} are not allowed for anonymous users"
            )

        super().__init__(logger)

        self.request_stats = Stats()
        self.db_engine = db_engine
        self.upstream = upstream
        self.read_only = read_only
        self.backfill_queue = None
        self.anon_perms = set(anon_perms)
        self.admin_username = admin_username
        self.admin_password = admin_password

        self.logger.info(
            "Anonymous user permissions are: %s", ", ".join(self.anon_perms)
        )

    def accept_client(self, socket):
        return ServerClient(socket, self)

    async def create_admin_user(self):
        admin_permissions = (ALL_PERM,)
        async with self.db_engine.connect(self.logger) as db:
            added = await db.new_user(
                self.admin_username,
                admin_permissions,
                hash_token(TOKEN_ALGORITHM, new_salt(), self.admin_password),
            )
            if added:
                self.logger.info("Created admin user '%s'", self.admin_username)
            else:
                await db.set_user_perms(
                    self.admin_username,
                    admin_permissions,
                )
                await db.set_user_token(
                    self.admin_username,
                    hash_token(TOKEN_ALGORITHM, new_salt(), self.admin_password),
                )
                self.logger.info("Admin user '%s' updated", self.admin_username)

    async def backfill_worker_task(self):
        async with await create_async_client(
            self.upstream
        ) as client, self.db_engine.connect(self.logger) as db:
            while True:
                item = await self.backfill_queue.get()
                if item is None:
                    self.backfill_queue.task_done()
                    break

                method, taskhash = item
                d = await client.get_taskhash(method, taskhash)
                if d is not None:
                    await db.insert_unihash(d["method"], d["taskhash"], d["unihash"])
                self.backfill_queue.task_done()

    def start(self):
        tasks = super().start()
        if self.upstream:
            self.backfill_queue = asyncio.Queue()
            tasks += [self.backfill_worker_task()]

        self.loop.run_until_complete(self.db_engine.create())

        if self.admin_username:
            self.loop.run_until_complete(self.create_admin_user())

        return tasks

    async def stop(self):
        if self.backfill_queue is not None:
            await self.backfill_queue.put(None)
        await super().stop()
