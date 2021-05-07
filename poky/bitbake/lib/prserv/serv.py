#
# SPDX-License-Identifier: GPL-2.0-only
#

import os,sys,logging
import signal, time
from xmlrpc.server import SimpleXMLRPCServer, SimpleXMLRPCRequestHandler
import socket
import io
import sqlite3
import bb.server.xmlrpcclient
import prserv
import prserv.db
import errno
import multiprocessing

logger = logging.getLogger("BitBake.PRserv")

class Handler(SimpleXMLRPCRequestHandler):
    def _dispatch(self,method,params):
        try:
            value=self.server.funcs[method](*params)
        except:
            import traceback
            traceback.print_exc()
            raise
        return value

PIDPREFIX = "/tmp/PRServer_%s_%s.pid"
singleton = None


class PRServer(SimpleXMLRPCServer):
    def __init__(self, dbfile, logfile, interface):
        ''' constructor '''
        try:
            SimpleXMLRPCServer.__init__(self, interface,
                                        logRequests=False, allow_none=True)
        except socket.error:
            ip=socket.gethostbyname(interface[0])
            port=interface[1]
            msg="PR Server unable to bind to %s:%s\n" % (ip, port)
            sys.stderr.write(msg)
            raise PRServiceConfigError

        self.dbfile=dbfile
        self.logfile=logfile
        self.host, self.port = self.socket.getsockname()

        self.register_function(self.getPR, "getPR")
        self.register_function(self.ping, "ping")
        self.register_function(self.export, "export")
        self.register_function(self.importone, "importone")
        self.register_introspection_functions()

        self.iter_count = 0
        # 60 iterations between syncs or sync if dirty every ~30 seconds
        self.iterations_between_sync = 60

    def sigint_handler(self, signum, stack):
        if self.table:
            self.table.sync()

    def sigterm_handler(self, signum, stack):
        if self.table:
            self.table.sync()
        raise(SystemExit)

    def process_request(self, request, client_address):
        if request is None:
            return
        try:
            self.finish_request(request, client_address)
            self.shutdown_request(request)
            self.iter_count = (self.iter_count + 1) % self.iterations_between_sync
            if self.iter_count == 0:
                self.table.sync_if_dirty()
        except:
            self.handle_error(request, client_address)
            self.shutdown_request(request)
            self.table.sync()
        self.table.sync_if_dirty()

    def serve_forever(self, poll_interval=0.5):
        signal.signal(signal.SIGINT, self.sigint_handler)
        signal.signal(signal.SIGTERM, self.sigterm_handler)

        self.db = prserv.db.PRData(self.dbfile)
        self.table = self.db["PRMAIN"]
        return super().serve_forever(poll_interval)

    def export(self, version=None, pkgarch=None, checksum=None, colinfo=True):
        try:
            return self.table.export(version, pkgarch, checksum, colinfo)
        except sqlite3.Error as exc:
            logger.error(str(exc))
            return None

    def importone(self, version, pkgarch, checksum, value):
        return self.table.importone(version, pkgarch, checksum, value)

    def ping(self):
        return True

    def getinfo(self):
        return (self.host, self.port)

    def getPR(self, version, pkgarch, checksum):
        try:
            return self.table.getValue(version, pkgarch, checksum)
        except prserv.NotFoundError:
            logger.error("can not find value for (%s, %s)",version, checksum)
            return None
        except sqlite3.Error as exc:
            logger.error(str(exc))
            return None

class PRServSingleton(object):
    def __init__(self, dbfile, logfile, interface):
        self.dbfile = dbfile
        self.logfile = logfile
        self.interface = interface
        self.host = None
        self.port = None

    def start(self):
        self.prserv = PRServer(self.dbfile, self.logfile, self.interface)
        self.process = multiprocessing.Process(target=self.prserv.serve_forever)
        self.process.start()

        self.host, self.port = self.prserv.getinfo()

    def getinfo(self):
        return (self.host, self.port)

class PRServerConnection(object):
    def __init__(self, host, port):
        if is_local_special(host, port):
            host, port = singleton.getinfo()
        self.host = host
        self.port = port
        self.connection, self.transport = bb.server.xmlrpcclient._create_server(self.host, self.port)

    def getPR(self, version, pkgarch, checksum):
        return self.connection.getPR(version, pkgarch, checksum)

    def ping(self):
        return self.connection.ping()

    def export(self,version=None, pkgarch=None, checksum=None, colinfo=True):
        return self.connection.export(version, pkgarch, checksum, colinfo)

    def importone(self, version, pkgarch, checksum, value):
        return self.connection.importone(version, pkgarch, checksum, value)

    def getinfo(self):
        return self.host, self.port

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

def start_daemon(dbfile, host, port, logfile):
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

    server = PRServer(os.path.abspath(dbfile), os.path.abspath(logfile), (ip,port))
    run_as_daemon(server.serve_forever, pidfile, os.path.abspath(logfile))

    # Sometimes, the port (i.e. localhost:0) indicated by the user does not match with
    # the one the server actually is listening, so at least warn the user about it
    _,rport = server.getinfo()
    if port != rport:
        sys.stdout.write("Server is listening at port %s instead of %s\n"
                         % (rport,port))
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
    if host.strip().upper() == 'localhost'.upper() and (not port):
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

    if is_local_special(host_params[0], int(host_params[1])):
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
            singleton = PRServSingleton(os.path.abspath(dbfile), os.path.abspath(logfile), ("localhost",0))
            singleton.start()
    if singleton:
        host, port = singleton.getinfo()
    else:
        host = host_params[0]
        port = int(host_params[1])

    try:
        connection = PRServerConnection(host,port)
        connection.ping()
        realhost, realport = connection.getinfo()
        return str(realhost) + ":" + str(realport)
        
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
    conn=PRServerConnection(host, port)
    return conn.ping()

def connect(host, port):
    return PRServerConnection(host, port)
