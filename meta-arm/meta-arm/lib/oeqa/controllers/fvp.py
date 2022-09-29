import asyncio
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
                 port=None, dir_image=None, rootfs=None, **kwargs):
        super().__init__(logger, target_ip, server_ip, timeout, user, port)
        image_dir = pathlib.Path(dir_image)
        # rootfs may have multiple extensions so we need to strip *all* suffixes
        basename = pathlib.Path(rootfs)
        basename = basename.name.replace("".join(basename.suffixes), "")
        self.fvpconf = image_dir / (basename + ".fvpconf")
        self.config = conffile.load(self.fvpconf)

        if not self.fvpconf.exists():
            raise FileNotFoundError(f"Cannot find {self.fvpconf}")

    async def boot_fvp(self):
        self.fvp = runner.FVPRunner(self.logger)
        await self.fvp.start(self.config)
        self.logger.debug(f"Started FVP PID {self.fvp.pid()}")
        await self._after_start()

    async def _after_start(self):
        pass

    async def _after_stop(self):
        pass

    async def stop_fvp(self):
        returncode = await self.fvp.stop()
        await self._after_stop()

        self.logger.debug(f"Stopped FVP with return code {returncode}")

    def start(self, **kwargs):
        # When we can assume Py3.7+, this can simply be asyncio.run()
        loop = asyncio.get_event_loop()
        loop.run_until_complete(asyncio.gather(self.boot_fvp()))

    def stop(self, **kwargs):
        loop = asyncio.get_event_loop()
        loop.run_until_complete(asyncio.gather(self.stop_fvp()))


class OEFVPTarget(OEFVPSSHTarget):
    """
    For compatibility with OE-core test cases, this target's start() method
    waits for a Linux shell before returning to ensure that SSH commands work
    with the default test dependencies.
    """
    def __init__(self, logger, target_ip, server_ip, bootlog=None, **kwargs):
        super().__init__(logger, target_ip, server_ip, **kwargs)
        self.logfile = bootlog and open(bootlog, "wb") or None

        # FVPs boot slowly, so allow ten minutes
        self.boot_timeout = 10 * 60

    async def _after_start(self):
        self.logger.debug(f"Awaiting console on terminal {self.config['consoles']['default']}")
        console = await self.fvp.create_pexpect(self.config['consoles']['default'])
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

    def __init__(self, logger, target_ip, server_ip, bootlog=None, **kwargs):
        super().__init__(logger, target_ip, server_ip, **kwargs)
        self.terminals = {}

        self.test_log_path = pathlib.Path(bootlog).parent
        self.test_log_suffix = pathlib.Path(bootlog).suffix
        self.bootlog = bootlog

    async def _add_terminal(self, name, fvp_name):
        logfile = self._create_logfile(name)
        self.logger.info(f'Creating terminal {name} on {fvp_name}')
        self.terminals[name] = \
            await self.fvp.create_pexpect(fvp_name, logfile=logfile)

    def _create_logfile(self, name):
        fvp_log_file = f"{name}_log{self.test_log_suffix}"
        fvp_log_path = pathlib.Path(self.test_log_path, fvp_log_file)
        fvp_log_symlink = pathlib.Path(self.test_log_path, f"{name}_log")
        try:
            os.remove(fvp_log_symlink)
        except:
            pass
        os.symlink(fvp_log_file, fvp_log_symlink)
        return open(fvp_log_path, 'wb')

    async def _after_start(self):
        for name, console in self.config["consoles"].items():
            await self._add_terminal(name, console)

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
