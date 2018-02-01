# Copyright (c) 2013-2014 Intel Corporation
#
# Released under the MIT license (see COPYING.MIT)

# DESCRIPTION
# This module is mainly used by scripts/oe-selftest and modules under meta/oeqa/selftest
# It provides a class and methods for running commands on the host in a convienent way for tests.



import os
import sys
import signal
import subprocess
import threading
import logging
from oeqa.utils import CommandError
from oeqa.utils import ftools
import re
import contextlib
# Export test doesn't require bb
try:
    import bb
except ImportError:
    pass

class Command(object):
    def __init__(self, command, bg=False, timeout=None, data=None, **options):

        self.defaultopts = {
            "stdout": subprocess.PIPE,
            "stderr": subprocess.STDOUT,
            "stdin": None,
            "shell": False,
            "bufsize": -1,
        }

        self.cmd = command
        self.bg = bg
        self.timeout = timeout
        self.data = data

        self.options = dict(self.defaultopts)
        if isinstance(self.cmd, str):
            self.options["shell"] = True
        if self.data:
            self.options['stdin'] = subprocess.PIPE
        self.options.update(options)

        self.status = None
        self.output = None
        self.error = None
        self.thread = None

        self.log = logging.getLogger("utils.commands")

    def run(self):
        self.process = subprocess.Popen(self.cmd, **self.options)

        def commThread():
            self.output, self.error = self.process.communicate(self.data)

        self.thread = threading.Thread(target=commThread)
        self.thread.start()

        self.log.debug("Running command '%s'" % self.cmd)

        if not self.bg:
            self.thread.join(self.timeout)
            self.stop()

    def stop(self):
        if self.thread.isAlive():
            self.process.terminate()
            # let's give it more time to terminate gracefully before killing it
            self.thread.join(5)
            if self.thread.isAlive():
                self.process.kill()
                self.thread.join()

        if not self.output:
            self.output = ""
        else:
            self.output = self.output.decode("utf-8", errors='replace').rstrip()
        self.status = self.process.poll()

        self.log.debug("Command '%s' returned %d as exit code." % (self.cmd, self.status))
        # logging the complete output is insane
        # bitbake -e output is really big
        # and makes the log file useless
        if self.status:
            lout = "\n".join(self.output.splitlines()[-20:])
            self.log.debug("Last 20 lines:\n%s" % lout)


class Result(object):
    pass


def runCmd(command, ignore_status=False, timeout=None, assert_error=True,
          native_sysroot=None, limit_exc_output=0, **options):
    result = Result()

    if native_sysroot:
        extra_paths = "%s/sbin:%s/usr/sbin:%s/usr/bin" % \
                      (native_sysroot, native_sysroot, native_sysroot)
        nenv = dict(options.get('env', os.environ))
        nenv['PATH'] = extra_paths + ':' + nenv.get('PATH', '')
        options['env'] = nenv

    cmd = Command(command, timeout=timeout, **options)
    cmd.run()

    result.command = command
    result.status = cmd.status
    result.output = cmd.output
    result.error = cmd.error
    result.pid = cmd.process.pid

    if result.status and not ignore_status:
        exc_output = result.output
        if limit_exc_output > 0:
            split = result.output.splitlines()
            if len(split) > limit_exc_output:
                exc_output = "\n... (last %d lines of output)\n" % limit_exc_output + \
                             '\n'.join(split[-limit_exc_output:])
        if assert_error:
            raise AssertionError("Command '%s' returned non-zero exit status %d:\n%s" % (command, result.status, exc_output))
        else:
            raise CommandError(result.status, command, exc_output)

    return result


def bitbake(command, ignore_status=False, timeout=None, postconfig=None, **options):

    if postconfig:
        postconfig_file = os.path.join(os.environ.get('BUILDDIR'), 'oeqa-post.conf')
        ftools.write_file(postconfig_file, postconfig)
        extra_args = "-R %s" % postconfig_file
    else:
        extra_args = ""

    if isinstance(command, str):
        cmd = "bitbake " + extra_args + " " + command
    else:
        cmd = [ "bitbake" ] + [a for a in (command + extra_args.split(" ")) if a not in [""]]

    try:
        return runCmd(cmd, ignore_status, timeout, **options)
    finally:
        if postconfig:
            os.remove(postconfig_file)


def get_bb_env(target=None, postconfig=None):
    if target:
        return bitbake("-e %s" % target, postconfig=postconfig).output
    else:
        return bitbake("-e", postconfig=postconfig).output

def get_bb_vars(variables=None, target=None, postconfig=None):
    """Get values of multiple bitbake variables"""
    bbenv = get_bb_env(target, postconfig=postconfig)

    if variables is not None:
        variables = variables.copy()
    var_re = re.compile(r'^(export )?(?P<var>\w+(_.*)?)="(?P<value>.*)"$')
    unset_re = re.compile(r'^unset (?P<var>\w+)$')
    lastline = None
    values = {}
    for line in bbenv.splitlines():
        match = var_re.match(line)
        val = None
        if match:
            val = match.group('value')
        else:
            match = unset_re.match(line)
            if match:
                # Handle [unexport] variables
                if lastline.startswith('#   "'):
                    val = lastline.split('"')[1]
        if val:
            var = match.group('var')
            if variables is None:
                values[var] = val
            else:
                if var in variables:
                    values[var] = val
                    variables.remove(var)
                # Stop after all required variables have been found
                if not variables:
                    break
        lastline = line
    if variables:
        # Fill in missing values
        for var in variables:
            values[var] = None
    return values

def get_bb_var(var, target=None, postconfig=None):
    return get_bb_vars([var], target, postconfig)[var]

def get_test_layer():
    layers = get_bb_var("BBLAYERS").split()
    testlayer = None
    for l in layers:
        if '~' in l:
            l = os.path.expanduser(l)
        if "/meta-selftest" in l and os.path.isdir(l):
            testlayer = l
            break
    return testlayer

def create_temp_layer(templayerdir, templayername, priority=999, recipepathspec='recipes-*/*'):
    os.makedirs(os.path.join(templayerdir, 'conf'))
    with open(os.path.join(templayerdir, 'conf', 'layer.conf'), 'w') as f:
        f.write('BBPATH .= ":${LAYERDIR}"\n')
        f.write('BBFILES += "${LAYERDIR}/%s/*.bb \\' % recipepathspec)
        f.write('            ${LAYERDIR}/%s/*.bbappend"\n' % recipepathspec)
        f.write('BBFILE_COLLECTIONS += "%s"\n' % templayername)
        f.write('BBFILE_PATTERN_%s = "^${LAYERDIR}/"\n' % templayername)
        f.write('BBFILE_PRIORITY_%s = "%d"\n' % (templayername, priority))
        f.write('BBFILE_PATTERN_IGNORE_EMPTY_%s = "1"\n' % templayername)


@contextlib.contextmanager
def runqemu(pn, ssh=True, runqemuparams='', image_fstype=None, launch_cmd=None, qemuparams=None, overrides={}, discard_writes=True):
    """
    launch_cmd means directly run the command, don't need set rootfs or env vars.
    """

    import bb.tinfoil
    import bb.build

    tinfoil = bb.tinfoil.Tinfoil()
    tinfoil.prepare(config_only=False, quiet=True)
    try:
        tinfoil.logger.setLevel(logging.WARNING)
        import oeqa.targetcontrol
        tinfoil.config_data.setVar("TEST_LOG_DIR", "${WORKDIR}/testimage")
        tinfoil.config_data.setVar("TEST_QEMUBOOT_TIMEOUT", "1000")
        # Tell QemuTarget() whether need find rootfs/kernel or not
        if launch_cmd:
            tinfoil.config_data.setVar("FIND_ROOTFS", '0')
        else:
            tinfoil.config_data.setVar("FIND_ROOTFS", '1')

        recipedata = tinfoil.parse_recipe(pn)
        for key, value in overrides.items():
            recipedata.setVar(key, value)

        # The QemuRunner log is saved out, but we need to ensure it is at the right
        # log level (and then ensure that since it's a child of the BitBake logger,
        # we disable propagation so we don't then see the log events on the console)
        logger = logging.getLogger('BitBake.QemuRunner')
        logger.setLevel(logging.DEBUG)
        logger.propagate = False
        logdir = recipedata.getVar("TEST_LOG_DIR")

        qemu = oeqa.targetcontrol.QemuTarget(recipedata, image_fstype)
    finally:
        # We need to shut down tinfoil early here in case we actually want
        # to run tinfoil-using utilities with the running QEMU instance.
        # Luckily QemuTarget doesn't need it after the constructor.
        tinfoil.shutdown()

    # Setup bitbake logger as console handler is removed by tinfoil.shutdown
    bblogger = logging.getLogger('BitBake')
    bblogger.setLevel(logging.INFO)
    console = logging.StreamHandler(sys.stdout)
    bbformat = bb.msg.BBLogFormatter("%(levelname)s: %(message)s")
    if sys.stdout.isatty():
        bbformat.enable_color()
    console.setFormatter(bbformat)
    bblogger.addHandler(console)

    try:
        qemu.deploy()
        try:
            qemu.start(params=qemuparams, ssh=ssh, runqemuparams=runqemuparams, launch_cmd=launch_cmd, discard_writes=discard_writes)
        except bb.build.FuncFailed:
            raise Exception('Failed to start QEMU - see the logs in %s' % logdir)

        yield qemu

    finally:
        try:
            qemu.stop()
        except:
            pass

def updateEnv(env_file):
    """
    Source a file and update environment.
    """

    cmd = ". %s; env -0" % env_file
    result = runCmd(cmd)

    for line in result.output.split("\0"):
        (key, _, value) = line.partition("=")
        os.environ[key] = value
