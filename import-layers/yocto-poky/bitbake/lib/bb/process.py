import logging
import signal
import subprocess
import errno
import select

logger = logging.getLogger('BitBake.Process')

def subprocess_setup():
    # Python installs a SIGPIPE handler by default. This is usually not what
    # non-Python subprocesses expect.
    signal.signal(signal.SIGPIPE, signal.SIG_DFL)

class CmdError(RuntimeError):
    def __init__(self, command, msg=None):
        self.command = command
        self.msg = msg

    def __str__(self):
        if not isinstance(self.command, str):
            cmd = subprocess.list2cmdline(self.command)
        else:
            cmd = self.command

        msg = "Execution of '%s' failed" % cmd
        if self.msg:
            msg += ': %s' % self.msg
        return msg

class NotFoundError(CmdError):
    def __str__(self):
        return CmdError.__str__(self) + ": command not found"

class ExecutionError(CmdError):
    def __init__(self, command, exitcode, stdout = None, stderr = None):
        CmdError.__init__(self, command)
        self.exitcode = exitcode
        self.stdout = stdout
        self.stderr = stderr

    def __str__(self):
        message = ""
        if self.stderr:
            message += self.stderr
        if self.stdout:
            message += self.stdout
        if message:
            message = ":\n" + message
        return (CmdError.__str__(self) +
                " with exit code %s" % self.exitcode + message)

class Popen(subprocess.Popen):
    defaults = {
        "close_fds": True,
        "preexec_fn": subprocess_setup,
        "stdout": subprocess.PIPE,
        "stderr": subprocess.STDOUT,
        "stdin": subprocess.PIPE,
        "shell": False,
    }

    def __init__(self, *args, **kwargs):
        options = dict(self.defaults)
        options.update(kwargs)
        subprocess.Popen.__init__(self, *args, **options)

def _logged_communicate(pipe, log, input, extrafiles):
    if pipe.stdin:
        if input is not None:
            pipe.stdin.write(input)
        pipe.stdin.close()

    outdata, errdata = [], []
    rin = []

    if pipe.stdout is not None:
        bb.utils.nonblockingfd(pipe.stdout.fileno())
        rin.append(pipe.stdout)
    if pipe.stderr is not None:
        bb.utils.nonblockingfd(pipe.stderr.fileno())
        rin.append(pipe.stderr)
    for fobj, _ in extrafiles:
        bb.utils.nonblockingfd(fobj.fileno())
        rin.append(fobj)

    def readextras(selected):
        for fobj, func in extrafiles:
            if fobj in selected:
                try:
                    data = fobj.read()
                except IOError as err:
                    if err.errno == errno.EAGAIN or err.errno == errno.EWOULDBLOCK:
                        data = None
                if data is not None:
                    func(data)

    def read_all_pipes(log, rin, outdata, errdata):
        rlist = rin
        stdoutbuf = b""
        stderrbuf = b""

        try:
            r,w,e = select.select (rlist, [], [], 1)
        except OSError as e:
            if e.errno != errno.EINTR:
                raise

        readextras(r)

        if pipe.stdout in r:
            data = stdoutbuf + pipe.stdout.read()
            if data is not None and len(data) > 0:
                try:
                    data = data.decode("utf-8")
                    outdata.append(data)
                    log.write(data)
                    log.flush()
                    stdoutbuf = b""
                except UnicodeDecodeError:
                    stdoutbuf = data

        if pipe.stderr in r:
            data = stderrbuf + pipe.stderr.read()
            if data is not None and len(data) > 0:
                try:
                    data = data.decode("utf-8")
                    errdata.append(data)
                    log.write(data)
                    log.flush()
                    stderrbuf = b""
                except UnicodeDecodeError:
                    stderrbuf = data

    try:
        # Read all pipes while the process is open
        while pipe.poll() is None:
            read_all_pipes(log, rin, outdata, errdata)

        # Pocess closed, drain all pipes...
        read_all_pipes(log, rin, outdata, errdata)
    finally:
        log.flush()

    if pipe.stdout is not None:
        pipe.stdout.close()
    if pipe.stderr is not None:
        pipe.stderr.close()
    return ''.join(outdata), ''.join(errdata)

def run(cmd, input=None, log=None, extrafiles=None, **options):
    """Convenience function to run a command and return its output, raising an
    exception when the command fails"""

    if not extrafiles:
        extrafiles = []

    if isinstance(cmd, str) and not "shell" in options:
        options["shell"] = True

    try:
        pipe = Popen(cmd, **options)
    except OSError as exc:
        if exc.errno == 2:
            raise NotFoundError(cmd)
        else:
            raise CmdError(cmd, exc)

    if log:
        stdout, stderr = _logged_communicate(pipe, log, input, extrafiles)
    else:
        stdout, stderr = pipe.communicate(input)
        if not stdout is None:
            stdout = stdout.decode("utf-8")
        if not stderr is None:
            stderr = stderr.decode("utf-8")

    if pipe.returncode != 0:
        raise ExecutionError(cmd, pipe.returncode, stdout, stderr)
    return stdout, stderr
