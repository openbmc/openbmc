import contextlib
import enum
import pathlib
import pexpect
import os
import time

from oeqa.core.target.ssh import OESSHTarget
from fvp import runner

class OEFVPTargetState(str, enum.Enum):
    OFF = "off"
    ON = "on"
    LINUX = "linux"


class OEFVPTarget(OESSHTarget):
    """
    For compatibility with OE-core test cases, this target's start() method
    waits for a Linux shell before returning to ensure that SSH commands work
    with the default test dependencies.
    """
    DEFAULT_CONSOLE = "default"

    def __init__(self, logger, target_ip, server_ip, timeout=300, user='root',
                 port=None, dir_image=None, rootfs=None, bootlog=None, **kwargs):
        super().__init__(logger, target_ip, server_ip, timeout, user, port)
        image_dir = pathlib.Path(dir_image)
        # rootfs may have multiple extensions so we need to strip *all* suffixes
        basename = pathlib.Path(rootfs)
        basename = basename.name.replace("".join(basename.suffixes), "")
        self.fvpconf = image_dir / (basename + ".fvpconf")
        if not self.fvpconf.exists():
            raise FileNotFoundError(f"Cannot find {self.fvpconf}")

        self.bootlog = bootlog
        self.terminals = {}
        self.stack = None
        self.state = OEFVPTargetState.OFF

    def transition(self, state, timeout=10*60):
        if state == self.state:
            return

        if state == OEFVPTargetState.OFF:
            returncode = self.fvp.stop()
            self.logger.debug(f"Stopped FVP with return code {returncode}")
            self.stack.close()
        elif state == OEFVPTargetState.ON:
            self.transition(OEFVPTargetState.OFF, timeout)
            self.stack = contextlib.ExitStack()
            self.fvp = runner.FVPRunner(self.logger)
            self.fvp_log = self._create_logfile("fvp", "wb")
            self.fvp.start(self.fvpconf, stdout=self.fvp_log)
            self.logger.debug(f"Started FVP PID {self.fvp.pid()}")
            self._setup_consoles()
        elif state == OEFVPTargetState.LINUX:
            self.transition(OEFVPTargetState.ON, timeout)
            try:
                self.expect(OEFVPTarget.DEFAULT_CONSOLE, "login\\:", timeout=timeout)
                self.logger.debug("Found login prompt")
                self.state = OEFVPTargetState.LINUX
            except pexpect.TIMEOUT:
                self.logger.info("Timed out waiting for login prompt.")
                self.logger.info("Boot log follows:")
                self.logger.info(b"\n".join(self.before(OEFVPTarget.DEFAULT_CONSOLE).splitlines()[-200:]).decode("utf-8", errors="replace"))
                raise RuntimeError("Failed to start FVP.")

        self.logger.info(f"Transitioned to {state}")
        self.state = state

    def start(self, **kwargs):
        # No-op - put the FVP in the required state lazily
        pass

    def stop(self, **kwargs):
        self.transition(OEFVPTargetState.OFF)

    def run(self, cmd, timeout=None):
        # Running a command implies the LINUX state
        self.transition(OEFVPTargetState.LINUX)
        return super().run(cmd, timeout)

    def _setup_consoles(self):
        with open(self.fvp_log.name, 'rb') as logfile:
            parser = runner.ConsolePortParser(logfile)
            config = self.fvp.getConfig()
            for name, console in config["consoles"].items():
                logfile = self._create_logfile(name)
                self.logger.info(f'Creating terminal {name} on {console}')
                port = parser.parse_port(console)
                self.terminals[name] = \
                    self.fvp.create_pexpect(port, logfile=logfile)

                # testimage.bbclass expects to see a log file at `bootlog`,
                # so make a symlink to the 'default' log file
                test_log_suffix = pathlib.Path(self.bootlog).suffix
                default_test_file = f"{name}_log{test_log_suffix}"
                if name == 'default' and not os.path.exists(self.bootlog):
                    os.symlink(default_test_file, self.bootlog)

    def _create_logfile(self, name, mode='ab'):
        if not self.bootlog:
            return None

        test_log_path = pathlib.Path(self.bootlog).parent
        test_log_suffix = pathlib.Path(self.bootlog).suffix
        fvp_log_file = f"{name}_log{test_log_suffix}"
        fvp_log_path = pathlib.Path(test_log_path, fvp_log_file)
        fvp_log_symlink = pathlib.Path(test_log_path, f"{name}_log")
        try:
            os.remove(fvp_log_symlink)
        except:
            pass
        os.symlink(fvp_log_file, fvp_log_symlink)
        return self.stack.enter_context(open(fvp_log_path, mode))

    def _get_terminal(self, name):
        return self.terminals[name]

    def __getattr__(self, name):
        """
        Magic method which automatically exposes the whole pexpect API on the
        target, with the first argument being the terminal name.

        e.g. self.target.expect(self.target.DEFAULT_CONSOLE, "login\\:")
        """
        def call_pexpect(terminal, *args, **kwargs):
            attr = getattr(self.terminals[terminal], name)
            if callable(attr):
                self.logger.debug(f"Calling {name} on {terminal} : with arguments -> {args}  :  {kwargs}")
                start_time = time.monotonic()  # Record the start time

                attr = getattr(self.terminals[terminal], name)
                result = attr(*args, **kwargs)

                end_time = time.monotonic()  # Record the end time
                elapsed_time = end_time - start_time
                self.logger.debug(f"Execution time for result: [ {result} ] - elapsed_time: {elapsed_time} seconds")
            else:
                result = attr

            return result

        return call_pexpect

    @property
    def config(self):
        return self.fvp.getConfig()
