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
import bb.asyncrpc

logger = logging.getLogger("BitBake.PRserv")

PIDPREFIX = "/tmp/PRServer_%s_%s.pid"
singleton = None

class PRServerClient(bb.asyncrpc.AsyncServerConnection):
    def __init__(self, reader, writer, table, read_only):
        super().__init__(reader, writer, 'PRSERVICE', logger)
        self.handlers.update({
            'get-pr': self.handle_get_pr,
            'import-one': self.handle_import_one,
            'export': self.handle_export,
            'is-readonly': self.handle_is_readonly,
        })
        self.table = table
        self.read_only = read_only

    def validate_proto_version(self):
        return (self.proto_version == (1, 0))

    async def dispatch_message(self, msg):
        try:
            await super().dispatch_message(msg)
        except:
            self.table.sync()
            raise

        self.table.sync_if_dirty()

    async def handle_get_pr(self, request):
        version = request['version']
        pkgarch = request['pkgarch']
        checksum = request['checksum']

        response = None
        try:
            value = self.table.getValue(version, pkgarch, checksum)
            response = {'value': value}
        except prserv.NotFoundError:
            logger.error("can not find value for (%s, %s)",version, checksum)
        except sqlite3.Error as exc:
            logger.error(str(exc))

        self.write_message(response)

    async def handle_import_one(self, request):
        response = None
        if not self.read_only:
            version = request['version']
            pkgarch = request['pkgarch']
            checksum = request['checksum']
            value = request['value']

            value = self.table.importone(version, pkgarch, checksum, value)
            if value is not None:
                response = {'value': value}

        self.write_message(response)

    async def handle_export(self, request):
        version = request['version']
        pkgarch = request['pkgarch']
        checksum = request['checksum']
        colinfo = request['colinfo']

        try:
            (metainfo, datainfo) = self.table.export(version, pkgarch, checksum, colinfo)
        except sqlite3.Error as exc:
            logger.error(str(exc))
            metainfo = datainfo = None

        response = {'metainfo': metainfo, 'datainfo': datainfo}
        self.write_message(response)

    async def handle_is_readonly(self, request):
        response = {'readonly': self.read_only}
        self.write_message(response)

class PRServer(bb.asyncrpc.AsyncServer):
    def __init__(self, dbfile, read_only=False):
        super().__init__(logger)
        self.dbfile = dbfile
        self.table = None
        self.read_only = read_only

    def accept_client(self, reader, writer):
        return PRServerClient(reader, writer, self.table, self.read_only)

    def _serve_forever(self):
        self.db = prserv.db.PRData(self.dbfile, read_only=self.read_only)
        self.table = self.db["PRMAIN"]

        logger.info("Started PRServer with DBfile: %s, Address: %s, PID: %s" %
                     (self.dbfile, self.address, str(os.getpid())))

        super()._serve_forever()

        self.table.sync_if_dirty()
        self.db.disconnect()

    def signal_handler(self):
        super().signal_handler()
        if self.table:
            self.table.sync()

class PRServSingleton(object):
    def __init__(self, dbfile, logfile, host, port):
        self.dbfile = dbfile
        self.logfile = logfile
        self.host = host
        self.port = port

    def start(self):
        self.prserv = PRServer(self.dbfile)
        self.prserv.start_tcp_server(socket.gethostbyname(self.host), self.port)
        self.process = self.prserv.serve_as_process()

        if not self.prserv.address:
            raise PRServiceConfigError
        if not self.port:
            self.port = int(self.prserv.address.rsplit(':', 1)[1])

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
    si = open('/dev/null', 'r')
    try:
        os.dup2(si.fileno(),sys.stdin.fileno())
    except (AttributeError, io.UnsupportedOperation):
        sys.stdin = si
    so = open(logfile, 'a+')
    try:
        os.dup2(so.fileno(),sys.stdout.fileno())
    except (AttributeError, io.UnsupportedOperation):
        sys.stdout = so
    try:
        os.dup2(so.fileno(),sys.stderr.fileno())
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
    with open(pidfile, 'w') as pf:
        pf.write("%s\n" % pid)

    func()
    os.remove(pidfile)
    os._exit(0)

def start_daemon(dbfile, host, port, logfile, read_only=False):
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
        server = PRServer(dbfile, read_only=read_only)
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
        for pf in glob.glob(PIDPREFIX % (ip,'*')):
            bn = os.path.basename(pf)
            root, _ = os.path.splitext(bn)
            ports.append(root.split('_')[-1])
        if len(ports):
            portstr = "Wrong port? Other ports listening at %s: %s" % (host, ' '.join(ports))

        sys.stderr.write("pidfile %s does not exist. Daemon not running? %s\n"
                         % (pidfile,portstr))
        return 1

    try:
        if is_running(pid):
            print("Sending SIGTERM to pr-server.")
            os.kill(pid, signal.SIGTERM)
            time.sleep(0.1)

        if os.path.exists(pidfile):
            os.remove(pidfile)

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
    if (host == 'localhost' or host == '127.0.0.1') and not port:
        return True
    else:
        return False

class PRServiceConfigError(Exception):
    pass

def auto_start(d):
    global singleton

    host_params = list(filter(None, (d.getVar('PRSERV_HOST') or '').split(':')))
    if not host_params:
        # Shutdown any existing PR Server
        auto_shutdown()
        return None

    if len(host_params) != 2:
        # Shutdown any existing PR Server
        auto_shutdown()
        logger.critical('\n'.join(['PRSERV_HOST: incorrect format',
                'Usage: PRSERV_HOST = "<hostname>:<port>"']))
        raise PRServiceConfigError

    host = host_params[0].strip().lower()
    port = int(host_params[1])
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
            singleton = PRServSingleton(os.path.abspath(dbfile), os.path.abspath(logfile), host, port)
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

    if host.strip().lower() == 'localhost' and not port:
        host = 'localhost'
        port = singleton.port

    conn = client.PRClient()
    conn.connect_tcp(host, port)
    return conn
