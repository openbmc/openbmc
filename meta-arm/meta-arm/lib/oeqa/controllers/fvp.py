import pathlib
import pexpect
import os

from oeqa.core.target.ssh import OESSHTarget
from fvp import conffile, runner


class OEFVPSSHTarget(OESSHTarget):
    """
    Base class for meta-arm FVP targets.
    Contains common logic to start and stop an FVP.
    """
    def __init__(self, logger, target_ip, server_ip, timeout=300, user='root',
                 port=None, dir_image=None, rootfs=None, bootlog=None, **kwargs):
        super().__init__(logger, target_ip, server_ip, timeout, user, port)
        image_dir = pathlib.Path(dir_image)
        # rootfs may have multiple extensions so we need to strip *all* suffixes
        basename = pathlib.Path(rootfs)
        basename = basename.name.replace("".join(basename.suffixes), "")
        self.fvpconf = image_dir / (basename + ".fvpconf")
        self.config = conffile.load(self.fvpconf)
        self.bootlog = bootlog

        if not self.fvpconf.exists():
            raise FileNotFoundError(f"Cannot find {self.fvpconf}")

    def _after_start(self):
        pass

    def start(self, **kwargs):
        self.fvp_log = self._create_logfile("fvp")
        self.fvp = runner.FVPRunner(self.logger)
        self.fvp.start(self.config, stdout=self.fvp_log)
        self.logger.debug(f"Started FVP PID {self.fvp.pid()}")
        self._after_start()

    def stop(self, **kwargs):
        returncode = self.fvp.stop()
        self.logger.debug(f"Stopped FVP with return code {returncode}")

    def _create_logfile(self, name):
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
        return open(fvp_log_path, 'wb')


class OEFVPTarget(OEFVPSSHTarget):
    """
    For compatibility with OE-core test cases, this target's start() method
    waits for a Linux shell before returning to ensure that SSH commands work
    with the default test dependencies.
    """
    def __init__(self, logger, target_ip, server_ip, **kwargs):
        super().__init__(logger, target_ip, server_ip, **kwargs)
        self.logfile = self.bootlog and open(self.bootlog, "wb") or None

        # FVPs boot slowly, so allow ten minutes
        self.boot_timeout = 10 * 60

    def _after_start(self):
        with open(self.fvp_log.name, 'rb') as logfile:
            parser = runner.ConsolePortParser(logfile)
            self.logger.debug(f"Awaiting console on terminal {self.config['consoles']['default']}")
            port = parser.parse_port(self.config['consoles']['default'])
            console = self.fvp.create_pexpect(port)
            try:
                console.expect("login\\:", timeout=self.boot_timeout)
                self.logger.debug("Found login prompt")
            except pexpect.TIMEOUT:
                self.logger.info("Timed out waiting for login prompt.")
                self.logger.info("Boot log follows:")
                self.logger.info(b"\n".join(console.before.splitlines()[-200:]).decode("utf-8", errors="replace"))
                raise RuntimeError("Failed to start FVP.")


class OEFVPSerialTarget(OEFVPSSHTarget):
    """
    This target is intended for interaction with the target over one or more
    telnet consoles using pexpect.

    This still depends on OEFVPSSHTarget so SSH commands can still be run on
    the target, but note that this class does not inherently guarantee that
    the SSH server is running prior to running test cases. Test cases that use
    SSH should first validate that SSH is available, e.g. by depending on the
    "linuxboot" test case in meta-arm.
    """
    DEFAULT_CONSOLE = "default"

    def __init__(self, logger, target_ip, server_ip, **kwargs):
        super().__init__(logger, target_ip, server_ip, **kwargs)
        self.terminals = {}

    def _after_start(self):
        with open(self.fvp_log.name, 'rb') as logfile:
            parser = runner.ConsolePortParser(logfile)
            for name, console in self.config["consoles"].items():
                logfile = self._create_logfile(name)
                self.logger.info(f'Creating terminal {name} on {console}')
                port = parser.parse_port(console)
                self.terminals[name] = \
                    self.fvp.create_pexpect(port, logfile=logfile)

                # testimage.bbclass expects to see a log file at `bootlog`,
                # so make a symlink to the 'default' log file
                if name == 'default':
                    default_test_file = f"{name}_log{self.test_log_suffix}"
                    os.symlink(default_test_file, self.bootlog)

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
                return attr(*args, **kwargs)
            else:
                return attr

        return call_pexpect
