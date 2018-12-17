import logging
import oe.classutils
import shlex
from bb.process import Popen, ExecutionError
from distutils.version import LooseVersion

logger = logging.getLogger('BitBake.OE.Terminal')


class UnsupportedTerminal(Exception):
    pass

class NoSupportedTerminals(Exception):
    def __init__(self, terms):
        self.terms = terms


class Registry(oe.classutils.ClassRegistry):
    command = None

    def __init__(cls, name, bases, attrs):
        super(Registry, cls).__init__(name.lower(), bases, attrs)

    @property
    def implemented(cls):
        return bool(cls.command)


class Terminal(Popen, metaclass=Registry):
    def __init__(self, sh_cmd, title=None, env=None, d=None):
        fmt_sh_cmd = self.format_command(sh_cmd, title)
        try:
            Popen.__init__(self, fmt_sh_cmd, env=env)
        except OSError as exc:
            import errno
            if exc.errno == errno.ENOENT:
                raise UnsupportedTerminal(self.name)
            else:
                raise

    def format_command(self, sh_cmd, title):
        fmt = {'title': title or 'Terminal', 'command': sh_cmd}
        if isinstance(self.command, str):
            return shlex.split(self.command.format(**fmt))
        else:
            return [element.format(**fmt) for element in self.command]

class XTerminal(Terminal):
    def __init__(self, sh_cmd, title=None, env=None, d=None):
        Terminal.__init__(self, sh_cmd, title, env, d)
        if not os.environ.get('DISPLAY'):
            raise UnsupportedTerminal(self.name)

class Gnome(XTerminal):
    command = 'gnome-terminal -t "{title}" -x {command}'
    priority = 2

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        # Recent versions of gnome-terminal does not support non-UTF8 charset:
        # https://bugzilla.gnome.org/show_bug.cgi?id=732127; as a workaround,
        # clearing the LC_ALL environment variable so it uses the locale.
        # Once fixed on the gnome-terminal project, this should be removed.
        if os.getenv('LC_ALL'): os.putenv('LC_ALL','')

        XTerminal.__init__(self, sh_cmd, title, env, d)

class Mate(XTerminal):
    command = 'mate-terminal --disable-factory -t "{title}" -x {command}'
    priority = 2

class Xfce(XTerminal):
    command = 'xfce4-terminal -T "{title}" -e "{command}"'
    priority = 2

class Terminology(XTerminal):
    command = 'terminology -T="{title}" -e {command}'
    priority = 2

class Konsole(XTerminal):
    command = 'konsole --separate --workdir . -p tabtitle="{title}" -e {command}'
    priority = 2

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        # Check version
        vernum = check_terminal_version("konsole")
        if vernum and LooseVersion(vernum) < '2.0.0':
            # Konsole from KDE 3.x
            self.command = 'konsole -T "{title}" -e {command}'
        elif vernum and LooseVersion(vernum) < '16.08.1':
            # Konsole pre 16.08.01 Has nofork
            self.command = 'konsole --nofork --workdir . -p tabtitle="{title}" -e {command}'
        XTerminal.__init__(self, sh_cmd, title, env, d)

class XTerm(XTerminal):
    command = 'xterm -T "{title}" -e {command}'
    priority = 1

class Rxvt(XTerminal):
    command = 'rxvt -T "{title}" -e {command}'
    priority = 1

class Screen(Terminal):
    command = 'screen -D -m -t "{title}" -S devshell {command}'

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        s_id = "devshell_%i" % os.getpid()
        self.command = "screen -D -m -t \"{title}\" -S %s {command}" % s_id
        Terminal.__init__(self, sh_cmd, title, env, d)
        msg = 'Screen started. Please connect in another terminal with ' \
            '"screen -r %s"' % s_id
        if (d):
            bb.event.fire(bb.event.LogExecTTY(msg, "screen -r %s" % s_id,
                                              0.5, 10), d)
        else:
            logger.warning(msg)

class TmuxRunning(Terminal):
    """Open a new pane in the current running tmux window"""
    name = 'tmux-running'
    command = 'tmux split-window "{command}"'
    priority = 2.75

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        if not bb.utils.which(os.getenv('PATH'), 'tmux'):
            raise UnsupportedTerminal('tmux is not installed')

        if not os.getenv('TMUX'):
            raise UnsupportedTerminal('tmux is not running')

        if not check_tmux_pane_size('tmux'):
            raise UnsupportedTerminal('tmux pane too small or tmux < 1.9 version is being used')

        Terminal.__init__(self, sh_cmd, title, env, d)

class TmuxNewWindow(Terminal):
    """Open a new window in the current running tmux session"""
    name = 'tmux-new-window'
    command = 'tmux new-window -n "{title}" "{command}"'
    priority = 2.70

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        if not bb.utils.which(os.getenv('PATH'), 'tmux'):
            raise UnsupportedTerminal('tmux is not installed')

        if not os.getenv('TMUX'):
            raise UnsupportedTerminal('tmux is not running')

        Terminal.__init__(self, sh_cmd, title, env, d)

class Tmux(Terminal):
    """Start a new tmux session and window"""
    command = 'tmux new -d -s devshell -n devshell "{command}"'
    priority = 0.75

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        if not bb.utils.which(os.getenv('PATH'), 'tmux'):
            raise UnsupportedTerminal('tmux is not installed')

        # TODO: consider using a 'devshell' session shared amongst all
        # devshells, if it's already there, add a new window to it.
        window_name = 'devshell-%i' % os.getpid()

        self.command = 'tmux new -d -s {0} -n {0} "{{command}}"'.format(window_name)
        Terminal.__init__(self, sh_cmd, title, env, d)

        attach_cmd = 'tmux att -t {0}'.format(window_name)
        msg = 'Tmux started. Please connect in another terminal with `tmux att -t {0}`'.format(window_name)
        if d:
            bb.event.fire(bb.event.LogExecTTY(msg, attach_cmd, 0.5, 10), d)
        else:
            logger.warning(msg)

class Custom(Terminal):
    command = 'false' # This is a placeholder
    priority = 3

    def __init__(self, sh_cmd, title=None, env=None, d=None):
        self.command = d and d.getVar('OE_TERMINAL_CUSTOMCMD')
        if self.command:
            if not '{command}' in self.command:
                self.command += ' {command}'
            Terminal.__init__(self, sh_cmd, title, env, d)
            logger.warning('Custom terminal was started.')
        else:
            logger.debug(1, 'No custom terminal (OE_TERMINAL_CUSTOMCMD) set')
            raise UnsupportedTerminal('OE_TERMINAL_CUSTOMCMD not set')


def prioritized():
    return Registry.prioritized()

def get_cmd_list():
    terms = Registry.prioritized()
    cmds = []
    for term in terms:
        if term.command:
            cmds.append(term.command)
    return cmds

def spawn_preferred(sh_cmd, title=None, env=None, d=None):
    """Spawn the first supported terminal, by priority"""
    for terminal in prioritized():
        try:
            spawn(terminal.name, sh_cmd, title, env, d)
            break
        except UnsupportedTerminal:
            continue
    else:
        raise NoSupportedTerminals(get_cmd_list())

def spawn(name, sh_cmd, title=None, env=None, d=None):
    """Spawn the specified terminal, by name"""
    logger.debug(1, 'Attempting to spawn terminal "%s"', name)
    try:
        terminal = Registry.registry[name]
    except KeyError:
        raise UnsupportedTerminal(name)

    # We need to know when the command completes but some terminals (at least
    # gnome and tmux) gives us no way to do this. We therefore write the pid
    # to a file using a "phonehome" wrapper script, then monitor the pid
    # until it exits.
    import tempfile
    import time
    pidfile = tempfile.NamedTemporaryFile(delete = False).name
    try:
        sh_cmd = bb.utils.which(os.getenv('PATH'), "oe-gnome-terminal-phonehome") + " " + pidfile + " " + sh_cmd
        pipe = terminal(sh_cmd, title, env, d)
        output = pipe.communicate()[0]
        if output:
            output = output.decode("utf-8")
        if pipe.returncode != 0:
            raise ExecutionError(sh_cmd, pipe.returncode, output)

        while os.stat(pidfile).st_size <= 0:
            time.sleep(0.01)
            continue
        with open(pidfile, "r") as f:
            pid = int(f.readline())
    finally:
        os.unlink(pidfile)

    while True:
        try:
            os.kill(pid, 0)
            time.sleep(0.1)
        except OSError:
           return

def check_tmux_pane_size(tmux):
    import subprocess as sub
    # On older tmux versions (<1.9), return false. The reason
    # is that there is no easy way to get the height of the active panel
    # on current window without nested formats (available from version 1.9)
    vernum = check_terminal_version("tmux")
    if vernum and LooseVersion(vernum) < '1.9':
        return False
    try:
        p = sub.Popen('%s list-panes -F "#{?pane_active,#{pane_height},}"' % tmux,
                shell=True,stdout=sub.PIPE,stderr=sub.PIPE)
        out, err = p.communicate()
        size = int(out.strip())
    except OSError as exc:
        import errno
        if exc.errno == errno.ENOENT:
            return None
        else:
            raise

    return size/2 >= 19

def check_terminal_version(terminalName):
    import subprocess as sub
    try:
        cmdversion = '%s --version' % terminalName
        if terminalName.startswith('tmux'):
            cmdversion = '%s -V' % terminalName
        newenv = os.environ.copy()
        newenv["LANG"] = "C"
        p = sub.Popen(['sh', '-c', cmdversion], stdout=sub.PIPE, stderr=sub.PIPE, env=newenv)
        out, err = p.communicate()
        ver_info = out.decode().rstrip().split('\n')
    except OSError as exc:
        import errno
        if exc.errno == errno.ENOENT:
            return None
        else:
            raise
    vernum = None
    for ver in ver_info:
        if ver.startswith('Konsole'):
            vernum = ver.split(' ')[-1]
        if ver.startswith('GNOME Terminal'):
            vernum = ver.split(' ')[-1]
        if ver.startswith('MATE Terminal'):
            vernum = ver.split(' ')[-1]
        if ver.startswith('tmux'):
            vernum = ver.split()[-1]
    return vernum

def distro_name():
    try:
        p = Popen(['lsb_release', '-i'])
        out, err = p.communicate()
        distro = out.split(':')[1].strip().lower()
    except:
        distro = "unknown"
    return distro
