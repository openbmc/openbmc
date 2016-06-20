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
        if isinstance(self.cmd, basestring):
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

        self.output = self.output.rstrip()
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


def runCmd(command, ignore_status=False, timeout=None, assert_error=True, **options):
    result = Result()

    cmd = Command(command, timeout=timeout, **options)
    cmd.run()

    result.command = command
    result.status = cmd.status
    result.output = cmd.output
    result.pid = cmd.process.pid

    if result.status and not ignore_status:
        if assert_error:
            raise AssertionError("Command '%s' returned non-zero exit status %d:\n%s" % (command, result.status, result.output))
        else:
            raise CommandError(result.status, command, result.output)

    return result


def bitbake(command, ignore_status=False, timeout=None, postconfig=None, **options):

    if postconfig:
        postconfig_file = os.path.join(os.environ.get('BUILDDIR'), 'oeqa-post.conf')
        ftools.write_file(postconfig_file, postconfig)
        extra_args = "-R %s" % postconfig_file
    else:
        extra_args = ""

    if isinstance(command, basestring):
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

def get_bb_var(var, target=None, postconfig=None):
    val = None
    bbenv = get_bb_env(target, postconfig=postconfig)
    lastline = None
    for line in bbenv.splitlines():
        if re.search("^(export )?%s=" % var, line):
            val = line.split('=', 1)[1]
            val = val.strip('\"')
            break
        elif re.match("unset %s$" % var, line):
            # Handle [unexport] variables
            if lastline.startswith('#   "'):
                val = lastline.split('\"')[1]
                break
        lastline = line
    return val

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
def runqemu(pn, ssh=True):

    import bb.tinfoil
    import bb.build

    tinfoil = bb.tinfoil.Tinfoil()
    tinfoil.prepare(False)
    try:
        tinfoil.logger.setLevel(logging.WARNING)
        import oeqa.targetcontrol
        tinfoil.config_data.setVar("TEST_LOG_DIR", "${WORKDIR}/testimage")
        tinfoil.config_data.setVar("TEST_QEMUBOOT_TIMEOUT", "1000")
        import oe.recipeutils
        recipefile = oe.recipeutils.pn_to_recipe(tinfoil.cooker, pn)
        recipedata = oe.recipeutils.parse_recipe(recipefile, [], tinfoil.config_data)

        # The QemuRunner log is saved out, but we need to ensure it is at the right
        # log level (and then ensure that since it's a child of the BitBake logger,
        # we disable propagation so we don't then see the log events on the console)
        logger = logging.getLogger('BitBake.QemuRunner')
        logger.setLevel(logging.DEBUG)
        logger.propagate = False
        logdir = recipedata.getVar("TEST_LOG_DIR", True)

        qemu = oeqa.targetcontrol.QemuTarget(recipedata)
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
            qemu.start(ssh=ssh)
        except bb.build.FuncFailed:
            raise Exception('Failed to start QEMU - see the logs in %s' % logdir)

        yield qemu

    finally:
        try:
            qemu.stop()
        except:
            pass
