#
# Copyright BitBake Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

import os,sys,logging
import signal, time
import socket
import io
import sqlite3
import prserv
import prserv.db
import errno
from . import create_async_client, revision_smaller, increase_revision
import bb.asyncrpc

logger = logging.getLogger("BitBake.PRserv")

PIDPREFIX = "/tmp/PRServer_%s_%s.pid"
singleton = None

class PRServerClient(bb.asyncrpc.AsyncServerConnection):
    def __init__(self, socket, server):
        super().__init__(socket, "PRSERVICE", server.logger)
        self.server = server

        self.handlers.update({
            "get-pr": self.handle_get_pr,
            "test-pr": self.handle_test_pr,
            "test-package": self.handle_test_package,
            "max-package-pr": self.handle_max_package_pr,
            "import-one": self.handle_import_one,
            "export": self.handle_export,
            "is-readonly": self.handle_is_readonly,
        })

    def validate_proto_version(self):
        return (self.proto_version == (1, 0))

    async def dispatch_message(self, msg):
        try:
            return await super().dispatch_message(msg)
        except:
            raise

    async def handle_test_pr(self, request):
        '''Finds the PR value corresponding to the request. If not found, returns None and doesn't insert a new value'''
        version = request["version"]
        pkgarch = request["pkgarch"]
        checksum = request["checksum"]
        history = request["history"]

        value = self.server.table.find_value(version, pkgarch, checksum, history)
        return {"value": value}

    async def handle_test_package(self, request):
        '''Tells whether there are entries for (version, pkgarch) in the db. Returns True or False'''
        version = request["version"]
        pkgarch = request["pkgarch"]

        value = self.server.table.test_package(version, pkgarch)
        return {"value": value}

    async def handle_max_package_pr(self, request):
        '''Finds the greatest PR value for (version, pkgarch) in the db. Returns None if no entry was found'''
        version = request["version"]
        pkgarch = request["pkgarch"]

        value = self.server.table.find_package_max_value(version, pkgarch)
        return {"value": value}

    async def handle_get_pr(self, request):
        version = request["version"]
        pkgarch = request["pkgarch"]
        checksum = request["checksum"]
        history = request["history"]

        if self.upstream_client is None:
            value = self.server.table.get_value(version, pkgarch, checksum, history)
            return {"value": value}

        # We have an upstream server.
        # Check whether the local server already knows the requested configuration.
        # If the configuration is a new one, the generated value we will add will
        # depend on what's on the upstream server. That's why we're calling find_value()
        # instead of get_value() directly.

        value = self.server.table.find_value(version, pkgarch, checksum, history)
        upstream_max = await self.upstream_client.max_package_pr(version, pkgarch)

        if value is not None:

            # The configuration is already known locally.

            if history:
                value = self.server.table.get_value(version, pkgarch, checksum, history)
            else:
                existing_value = value
                # In "no history", we need to make sure the value doesn't decrease
                # and is at least greater than the maximum upstream value
                # and the maximum local value

                local_max = self.server.table.find_package_max_value(version, pkgarch)
                if revision_smaller(value, local_max):
                    value = increase_revision(local_max)

                if revision_smaller(value, upstream_max):
                    # Ask upstream whether it knows the checksum
                    upstream_value = await self.upstream_client.test_pr(version, pkgarch, checksum)
                    if upstream_value is None:
                        # Upstream doesn't have our checksum, let create a new one
                        value = upstream_max + ".0"
                    else:
                        # Fine to take the same value as upstream
                        value = upstream_max

                if not value == existing_value and not self.server.read_only:
                    self.server.table.store_value(version, pkgarch, checksum, value)

            return {"value": value}

        # The configuration is a new one for the local server
        # Let's ask the upstream server whether it knows it

        known_upstream = await self.upstream_client.test_package(version, pkgarch)

        if not known_upstream:

            # The package is not known upstream, must be a local-only package
            # Let's compute the PR number using the local-only method

            value = self.server.table.get_value(version, pkgarch, checksum, history)
            return {"value": value}

        # The package is known upstream, let's ask the upstream server
        # whether it knows our new output hash

        value = await self.upstream_client.test_pr(version, pkgarch, checksum)

        if value is not None:

            # Upstream knows this output hash, let's store it and use it too.

            if not self.server.read_only:
                self.server.table.store_value(version, pkgarch, checksum, value)
            # If the local server is read only, won't be able to store the new
            # value in the database and will have to keep asking the upstream server
            return {"value": value}

        # The output hash doesn't exist upstream, get the most recent number from upstream (x)
        # Then, we want to have a new PR value for the local server: x.y

        upstream_max = await self.upstream_client.max_package_pr(version, pkgarch)
        # Here we know that the package is known upstream, so upstream_max can't be None
        subvalue = self.server.table.find_new_subvalue(version, pkgarch, upstream_max)

        if not self.server.read_only:
            self.server.table.store_value(version, pkgarch, checksum, subvalue)

        return {"value": subvalue}

    async def process_requests(self):
        if self.server.upstream is not None:
            self.upstream_client = await create_async_client(self.server.upstream)
        else:
            self.upstream_client = None

        try:
            await super().process_requests()
        finally:
            if self.upstream_client is not None:
                await self.upstream_client.close()

    async def handle_import_one(self, request):
        response = None
        if not self.server.read_only:
            version = request["version"]
            pkgarch = request["pkgarch"]
            checksum = request["checksum"]
            value = request["value"]

            value = self.server.table.importone(version, pkgarch, checksum, value)
            if value is not None:
                response = {"value": value}

        return response

    async def handle_export(self, request):
        version = request["version"]
        pkgarch = request["pkgarch"]
        checksum = request["checksum"]
        colinfo = request["colinfo"]
        history = request["history"]

        try:
            (metainfo, datainfo) = self.server.table.export(version, pkgarch, checksum, colinfo, history)
        except sqlite3.Error as exc:
            self.logger.error(str(exc))
            metainfo = datainfo = None

        return {"metainfo": metainfo, "datainfo": datainfo}

    async def handle_is_readonly(self, request):
        return {"readonly": self.server.read_only}

class PRServer(bb.asyncrpc.AsyncServer):
    def __init__(self, dbfile, read_only=False, upstream=None):
        super().__init__(logger)
        self.dbfile = dbfile
        self.table = None
        self.read_only = read_only
        self.upstream = upstream

    def accept_client(self, socket):
        return PRServerClient(socket, self)

    def start(self):
        tasks = super().start()
        self.db = prserv.db.PRData(self.dbfile, read_only=self.read_only)
        self.table = self.db["PRMAIN"]

        self.logger.info("Started PRServer with DBfile: %s, Address: %s, PID: %s" %
                     (self.dbfile, self.address, str(os.getpid())))

        if self.upstream is not None:
            self.logger.info("And upstream PRServer: %s " % (self.upstream))

        return tasks

    async def stop(self):
        self.db.disconnect()
        await super().stop()

class PRServSingleton(object):
    def __init__(self, dbfile, logfile, host, port, upstream):
        self.dbfile = dbfile
        self.logfile = logfile
        self.host = host
        self.port = port
        self.upstream = upstream

    def start(self):
        self.prserv = PRServer(self.dbfile, upstream=self.upstream)
        self.prserv.start_tcp_server(socket.gethostbyname(self.host), self.port)
        self.process = self.prserv.serve_as_process(log_level=logging.WARNING)

        if not self.prserv.address:
            raise PRServiceConfigError
        if not self.port:
            self.port = int(self.prserv.address.rsplit(":", 1)[1])

def run_as_daemon(func, pidfile, logfile):
    """
    See Advanced Programming in the UNIX, Sec 13.3
    """
    try:
        pid = os.fork()
        if pid > 0:
            os.waitpid(pid, 0)
            #parent return instead of exit to give control
            return pid
    except OSError as e:
        raise Exception("%s [%d]" % (e.strerror, e.errno))

    os.setsid()
    """
    fork again to make sure the daemon is not session leader,
    which prevents it from acquiring controlling terminal
    """
    try:
        pid = os.fork()
        if pid > 0: #parent
            os._exit(0)
    except OSError as e:
        raise Exception("%s [%d]" % (e.strerror, e.errno))

    os.chdir("/")

    sys.stdout.flush()
    sys.stderr.flush()

    # We could be called from a python thread with io.StringIO as
    # stdout/stderr or it could be 'real' unix fd forking where we need
    # to physically close the fds to prevent the program launching us from
    # potentially hanging on a pipe. Handle both cases.
    si = open("/dev/null", "r")
    try:
        os.dup2(si.fileno(), sys.stdin.fileno())
    except (AttributeError, io.UnsupportedOperation):
        sys.stdin = si
    so = open(logfile, "a+")
    try:
        os.dup2(so.fileno(), sys.stdout.fileno())
    except (AttributeError, io.UnsupportedOperation):
        sys.stdout = so
    try:
        os.dup2(so.fileno(), sys.stderr.fileno())
    except (AttributeError, io.UnsupportedOperation):
        sys.stderr = so

    # Clear out all log handlers prior to the fork() to avoid calling
    # event handlers not part of the PRserver
    for logger_iter in logging.Logger.manager.loggerDict.keys():
        logging.getLogger(logger_iter).handlers = []

    # Ensure logging makes it to the logfile
    streamhandler = logging.StreamHandler()
    streamhandler.setLevel(logging.DEBUG)
    formatter = bb.msg.BBLogFormatter("%(levelname)s: %(message)s")
    streamhandler.setFormatter(formatter)
    logger.addHandler(streamhandler)

    # write pidfile
    pid = str(os.getpid())
    with open(pidfile, "w") as pf:
        pf.write("%s\n" % pid)

    func()
    os.remove(pidfile)
    os._exit(0)

def start_daemon(dbfile, host, port, logfile, read_only=False, upstream=None):
    ip = socket.gethostbyname(host)
    pidfile = PIDPREFIX % (ip, port)
    try:
        with open(pidfile) as pf:
            pid = int(pf.readline().strip())
    except IOError:
        pid = None

    if pid:
        sys.stderr.write("pidfile %s already exist. Daemon already running?\n"
                            % pidfile)
        return 1

    dbfile = os.path.abspath(dbfile)
    def daemon_main():
        server = PRServer(dbfile, read_only=read_only, upstream=upstream)
        server.start_tcp_server(ip, port)
        server.serve_forever()

    run_as_daemon(daemon_main, pidfile, os.path.abspath(logfile))
    return 0

def stop_daemon(host, port):
    import glob
    ip = socket.gethostbyname(host)
    pidfile = PIDPREFIX % (ip, port)
    try:
        with open(pidfile) as pf:
            pid = int(pf.readline().strip())
    except IOError:
        pid = None

    if not pid:
        # when server starts at port=0 (i.e. localhost:0), server actually takes another port,
        # so at least advise the user which ports the corresponding server is listening
        ports = []
        portstr = ""
        for pf in glob.glob(PIDPREFIX % (ip, "*")):
            bn = os.path.basename(pf)
            root, _ = os.path.splitext(bn)
            ports.append(root.split("_")[-1])
        if len(ports):
            portstr = "Wrong port? Other ports listening at %s: %s" % (host, " ".join(ports))

        sys.stderr.write("pidfile %s does not exist. Daemon not running? %s\n"
                         % (pidfile, portstr))
        return 1

    try:
        if is_running(pid):
            print("Sending SIGTERM to pr-server.")
            os.kill(pid, signal.SIGTERM)
            time.sleep(0.1)

        try:
            os.remove(pidfile)
        except FileNotFoundError:
            # The PID file might have been removed by the exiting process
            pass

    except OSError as e:
        err = str(e)
        if err.find("No such process") <= 0:
            raise e

    return 0

def is_running(pid):
    try:
        os.kill(pid, 0)
    except OSError as err:
        if err.errno == errno.ESRCH:
            return False
    return True

def is_local_special(host, port):
    if (host == "localhost" or host == "127.0.0.1") and not port:
        return True
    else:
        return False

class PRServiceConfigError(Exception):
    pass

def auto_start(d):
    global singleton

    host_params = list(filter(None, (d.getVar("PRSERV_HOST") or "").split(":")))
    if not host_params:
        # Shutdown any existing PR Server
        auto_shutdown()
        return None

    if len(host_params) != 2:
        # Shutdown any existing PR Server
        auto_shutdown()
        logger.critical("\n".join(["PRSERV_HOST: incorrect format",
                'Usage: PRSERV_HOST = "<hostname>:<port>"']))
        raise PRServiceConfigError

    host = host_params[0].strip().lower()
    port = int(host_params[1])

    upstream = d.getVar("PRSERV_UPSTREAM") or None

    if is_local_special(host, port):
        import bb.utils
        cachedir = (d.getVar("PERSISTENT_DIR") or d.getVar("CACHE"))
        if not cachedir:
            logger.critical("Please set the 'PERSISTENT_DIR' or 'CACHE' variable")
            raise PRServiceConfigError
        dbfile = os.path.join(cachedir, "prserv.sqlite3")
        logfile = os.path.join(cachedir, "prserv.log")
        if singleton:
            if singleton.dbfile != dbfile:
               # Shutdown any existing PR Server as doesn't match config
               auto_shutdown()
        if not singleton:
            bb.utils.mkdirhier(cachedir)
            singleton = PRServSingleton(os.path.abspath(dbfile), os.path.abspath(logfile), host, port, upstream)
            singleton.start()
    if singleton:
        host = singleton.host
        port = singleton.port

    try:
        ping(host, port)
        return str(host) + ":" + str(port)

    except Exception:
        logger.critical("PRservice %s:%d not available" % (host, port))
        raise PRServiceConfigError

def auto_shutdown():
    global singleton
    if singleton and singleton.process:
        singleton.process.terminate()
        singleton.process.join()
        singleton = None

def ping(host, port):
    from . import client

    with client.PRClient() as conn:
        conn.connect_tcp(host, port)
        return conn.ping()

def connect(host, port):
    from . import client

    global singleton

    if host.strip().lower() == "localhost" and not port:
        host = "localhost"
        port = singleton.port

    conn = client.PRClient()
    conn.connect_tcp(host, port)
    return conn
