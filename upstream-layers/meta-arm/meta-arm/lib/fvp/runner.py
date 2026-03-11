import re
import subprocess
import os
import shlex
import shutil
import sys

from .terminal import terminals
from .conffile import load

def cli_from_config(config, terminal_choice):
    cli = []
    if config["fvp-bindir"]:
        cli.append(os.path.join(config["fvp-bindir"], config["exe"]))
    else:
        cli.append(config["exe"])

    for param, value in config["parameters"].items():
        cli.extend(["--parameter", f"{param}={value}"])

    for value in config["data"]:
        cli.extend(["--data", value])

    for param, value in config["applications"].items():
        cli.extend(["--application", f"{param}={value}"])

    for terminal, name in config["terminals"].items():
        # If terminals are enabled and this terminal has been named
        if terminal_choice != "none" and name:
            # TODO if raw mode
            # cli.extend(["--parameter", f"{terminal}.mode=raw"])
            cli.extend(["--parameter", f"{terminal}.terminal_command={terminals[terminal_choice].command.format(name=name)}"])
        else:
            # Disable terminal
            cli.extend(["--parameter", f"{terminal}.start_telnet=0"])

    cli.extend(config["args"])

    return cli

def check_telnet():
    # Check that telnet is present
    if not bool(shutil.which("telnet")):
        raise RuntimeError("Cannot find telnet, this is needed to connect to the FVP.")


class ConsolePortParser:
    def __init__(self, lines):
        self._lines = lines
        self._console_ports = {}

    def parse_port(self, console):
        if console in self._console_ports:
            return self._console_ports[console]

        while True:
            try:
                line = next(self._lines).strip().decode(errors='ignore')
                m = re.search(r"(\S+): Listening for serial connection on port (\d+)$", line)
                if m:
                    matched_console = m.group(1)
                    matched_port = int(m.group(2))
                    if matched_console == console:
                        return matched_port
                    else:
                        self._console_ports[matched_console] = matched_port
            except StopIteration:
                # self._lines might be a growing log file
                pass


# This function is backported from Python 3.8. Remove it and replace call sites
# with shlex.join once OE-core support for earlier Python versions is dropped.
def shlex_join(split_command):
    """Return a shell-escaped string from *split_command*."""
    return ' '.join(shlex.quote(arg) for arg in split_command)


class FVPRunner:
    def __init__(self, logger):
        self._logger = logger
        self._fvp_process = None
        self._telnets = []
        self._pexpects = []
        self._config = None

    def start(self, fvpconf, extra_args=[], terminal_choice="none", stdout=subprocess.PIPE):
        self._logger.debug(f"Loading {fvpconf}")
        self._config = load(fvpconf)

        cli = cli_from_config(self._config, terminal_choice)
        cli += extra_args

        # Pass through environment variables needed for GUI applications, such
        # as xterm, to work.
        env = self._config['env']
        for name in ('DISPLAY', 'PATH', 'WAYLAND_DISPLAY', 'XAUTHORITY'):
            if name in os.environ:
                env[name] = os.environ[name]

        # Allow filepath to be relative to fvp configuration file
        cwd = os.path.dirname(fvpconf) or None
        self._logger.debug(f"FVP call will be executed in working directory: {cwd}")

        self._logger.debug(f"Constructed FVP call: {shlex_join(cli)}")
        self._fvp_process = subprocess.Popen(
            cli,
            stdin=subprocess.DEVNULL, stdout=stdout, stderr=subprocess.STDOUT,
            env=env,
            cwd=cwd)

    def stop(self):
        if self._fvp_process:
            self._logger.debug(f"Terminating FVP PID {self._fvp_process.pid}")
            try:
                self._fvp_process.terminate()
                self._fvp_process.wait(10.0)
            except subprocess.TimeoutExpired:
                self._logger.debug(f"Killing FVP PID {self._fvp_process.pid}")
                self._fvp_process.kill()
            except ProcessLookupError:
                pass

        for telnet in self._telnets:
            try:
                telnet.terminate()
                telnet.wait(10.0)
            except subprocess.TimeoutExpired:
                telnet.kill()
            except ProcessLookupError:
                pass

        for console in self._pexpects:
            import pexpect
            # Ensure pexpect logs all remaining output to the logfile
            try:
                console.expect(pexpect.EOF, timeout=30.0)
            except pexpect.TIMEOUT:
                pexpect_logfile = ""
                if console.logfile is not None:
                    pexpect_logfile = f" ({console.logfile})"
                self._logger.debug(f"Unable to get EOF on pexpect spawn obj{pexpect_logfile}.")
            console.close(force=True)

        if self._fvp_process and self._fvp_process.returncode and \
                self._fvp_process.returncode > 0:
            # Return codes < 0 indicate that the process was explicitly
            # terminated above.
            self._logger.info(f"FVP quit with code {self._fvp_process.returncode}")
            return self._fvp_process.returncode
        else:
            return 0

    def wait(self, timeout):
        self._fvp_process.wait(timeout)

    def getConfig(self):
        return self._config

    @property
    def stdout(self):
        return self._fvp_process.stdout

    def create_telnet(self, port):
        check_telnet()
        telnet = subprocess.Popen(["telnet", "localhost", str(port)], stdin=sys.stdin, stdout=sys.stdout)
        self._telnets.append(telnet)
        return telnet

    def create_pexpect(self, port, **kwargs):
        import pexpect
        instance = pexpect.spawn(f"telnet localhost {port}", **kwargs)
        self._pexpects.append(instance)
        return instance

    def pid(self):
        return self._fvp_process.pid
