import asyncio
import re
import subprocess
import os
import shutil
import sys

from .terminal import terminals


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
            # TODO put name into terminal title
            cli.extend(["--parameter", f"{terminal}.terminal_command={terminals[terminal_choice].command}"])
        else:
            # Disable terminal
            cli.extend(["--parameter", f"{terminal}.start_telnet=0"])

    cli.extend(config["args"])

    return cli

def check_telnet():
    # Check that telnet is present
    if not bool(shutil.which("telnet")):
        raise RuntimeError("Cannot find telnet, this is needed to connect to the FVP.")

class FVPRunner:
    def __init__(self, logger):
        self._terminal_ports = {}
        self._line_callbacks = []
        self._logger = logger
        self._fvp_process = None
        self._telnets = []
        self._pexpects = []

    def add_line_callback(self, callback):
        self._line_callbacks.append(callback)

    async def start(self, config, extra_args=[], terminal_choice="none"):
        cli = cli_from_config(config, terminal_choice)
        cli += extra_args

        # Pass through environment variables needed for GUI applications, such
        # as xterm, to work.
        env = config['env']
        for name in ('DISPLAY', 'WAYLAND_DISPLAY'):
            if name in os.environ:
                env[name] = os.environ[name]

        self._logger.debug(f"Constructed FVP call: {cli}")
        self._fvp_process = await asyncio.create_subprocess_exec(
            *cli,
            stdin=subprocess.DEVNULL, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            env=env)

        def detect_terminals(line):
            m = re.match(r"^(\S+): Listening for serial connection on port (\d+)$", line)
            if m:
                terminal = m.group(1)
                port = int(m.group(2))
                self._terminal_ports[terminal] = port
        self.add_line_callback(detect_terminals)

    async def stop(self):
        if self._fvp_process:
            self._logger.debug(f"Terminating FVP PID {self._fvp_process.pid}")
            try:
                self._fvp_process.terminate()
                await asyncio.wait_for(self._fvp_process.wait(), 10.0)
            except asyncio.TimeoutError:
                self._logger.debug(f"Killing FVP PID {self._fvp_process.pid}")
                self._fvp_process.kill()
            except ProcessLookupError:
                pass

        for telnet in self._telnets:
            try:
                telnet.terminate()
                await asyncio.wait_for(telnet.wait(), 10.0)
            except asyncio.TimeoutError:
                telnet.kill()
            except ProcessLookupError:
                pass

        for console in self._pexpects:
            import pexpect
            # Ensure pexpect logs all remaining output to the logfile
            console.expect(pexpect.EOF, timeout=5.0)
            console.close()

        if self._fvp_process and self._fvp_process.returncode and \
                self._fvp_process.returncode > 0:
            # Return codes < 0 indicate that the process was explicitly
            # terminated above.
            self._logger.info(f"FVP quit with code {self._fvp_process.returncode}")
            return self._fvp_process.returncode
        else:
            return 0

    async def run(self, until=None):
        if until and until():
            return

        async for line in self._fvp_process.stdout:
            line = line.strip().decode("utf-8", errors="replace")
            for callback in self._line_callbacks:
                callback(line)
            if until and until():
                return

    async def _get_terminal_port(self, terminal, timeout):
        def terminal_exists():
            return terminal in self._terminal_ports
        await asyncio.wait_for(self.run(terminal_exists), timeout)
        return self._terminal_ports[terminal]

    async def create_telnet(self, terminal, timeout=15.0):
        check_telnet()
        port = await self._get_terminal_port(terminal, timeout)
        telnet = await asyncio.create_subprocess_exec("telnet", "localhost", str(port), stdin=sys.stdin, stdout=sys.stdout)
        self._telnets.append(telnet)
        return telnet

    async def create_pexpect(self, terminal, timeout=15.0, **kwargs):
        check_telnet()
        import pexpect
        port = await self._get_terminal_port(terminal, timeout)
        instance = pexpect.spawn(f"telnet localhost {port}", **kwargs)
        self._pexpects.append(instance)
        return instance

    def pid(self):
        return self._fvp_process.pid
