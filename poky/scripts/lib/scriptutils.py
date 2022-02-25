# Script utility functions
#
# Copyright (C) 2014 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-only
#

import argparse
import glob
import logging
import os
import random
import shlex
import shutil
import string
import subprocess
import sys
import tempfile
import threading
import importlib
import importlib.machinery
import importlib.util

class KeepAliveStreamHandler(logging.StreamHandler):
    def __init__(self, keepalive=True, **kwargs):
        super().__init__(**kwargs)
        if keepalive is True:
            keepalive = 5000 # default timeout
        self._timeout = threading.Condition()
        self._stop = False

        # background thread waits on condition, if the condition does not
        # happen emit a keep alive message
        def thread():
            while not self._stop:
                with self._timeout:
                    if not self._timeout.wait(keepalive):
                        self.emit(logging.LogRecord("keepalive", logging.INFO,
                            None, None, "Keepalive message", None, None))

        self._thread = threading.Thread(target = thread, daemon = True)
        self._thread.start()

    def close(self):
        # mark the thread to stop and notify it
        self._stop = True
        with self._timeout:
            self._timeout.notify()
        # wait for it to join
        self._thread.join()
        super().close()

    def emit(self, record):
        super().emit(record)
        # trigger timer reset
        with self._timeout:
            self._timeout.notify()

def logger_create(name, stream=None, keepalive=None):
    logger = logging.getLogger(name)
    if keepalive is not None:
        loggerhandler = KeepAliveStreamHandler(stream=stream, keepalive=keepalive)
    else:
        loggerhandler = logging.StreamHandler(stream=stream)
    loggerhandler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
    logger.addHandler(loggerhandler)
    logger.setLevel(logging.INFO)
    return logger

def logger_setup_color(logger, color='auto'):
    from bb.msg import BBLogFormatter

    for handler in logger.handlers:
        if (isinstance(handler, logging.StreamHandler) and
            isinstance(handler.formatter, BBLogFormatter)):
            if color == 'always' or (color == 'auto' and handler.stream.isatty()):
                handler.formatter.enable_color()


def load_plugins(logger, plugins, pluginpath):

    def load_plugin(name):
        logger.debug('Loading plugin %s' % name)
        spec = importlib.machinery.PathFinder.find_spec(name, path=[pluginpath] )
        if spec:
            mod = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(mod)
            return mod

    def plugin_name(filename):
        return os.path.splitext(os.path.basename(filename))[0]

    known_plugins = [plugin_name(p.__name__) for p in plugins]
    logger.debug('Loading plugins from %s...' % pluginpath)
    for fn in glob.glob(os.path.join(pluginpath, '*.py')):
        name = plugin_name(fn)
        if name != '__init__' and name not in known_plugins:
            plugin = load_plugin(name)
            if hasattr(plugin, 'plugin_init'):
                plugin.plugin_init(plugins)
            plugins.append(plugin)


def git_convert_standalone_clone(repodir):
    """If specified directory is a git repository, ensure it's a standalone clone"""
    import bb.process
    if os.path.exists(os.path.join(repodir, '.git')):
        alternatesfile = os.path.join(repodir, '.git', 'objects', 'info', 'alternates')
        if os.path.exists(alternatesfile):
            # This will have been cloned with -s, so we need to convert it so none
            # of the contents is shared
            bb.process.run('git repack -a', cwd=repodir)
            os.remove(alternatesfile)

def _get_temp_recipe_dir(d):
    # This is a little bit hacky but we need to find a place where we can put
    # the recipe so that bitbake can find it. We're going to delete it at the
    # end so it doesn't really matter where we put it.
    bbfiles = d.getVar('BBFILES').split()
    fetchrecipedir = None
    for pth in bbfiles:
        if pth.endswith('.bb'):
            pthdir = os.path.dirname(pth)
            if os.access(os.path.dirname(os.path.dirname(pthdir)), os.W_OK):
                fetchrecipedir = pthdir.replace('*', 'recipetool')
                if pthdir.endswith('workspace/recipes/*'):
                    # Prefer the workspace
                    break
    return fetchrecipedir

class FetchUrlFailure(Exception):
    def __init__(self, url):
        self.url = url
    def __str__(self):
        return "Failed to fetch URL %s" % self.url

def fetch_url(tinfoil, srcuri, srcrev, destdir, logger, preserve_tmp=False, mirrors=False):
    """
    Fetch the specified URL using normal do_fetch and do_unpack tasks, i.e.
    any dependencies that need to be satisfied in order to support the fetch
    operation will be taken care of
    """

    import bb

    checksums = {}
    fetchrecipepn = None

    # We need to put our temp directory under ${BASE_WORKDIR} otherwise
    # we may have problems with the recipe-specific sysroot population
    tmpparent = tinfoil.config_data.getVar('BASE_WORKDIR')
    bb.utils.mkdirhier(tmpparent)
    tmpdir = tempfile.mkdtemp(prefix='recipetool-', dir=tmpparent)
    try:
        tmpworkdir = os.path.join(tmpdir, 'work')
        logger.debug('fetch_url: temp dir is %s' % tmpdir)

        fetchrecipedir = _get_temp_recipe_dir(tinfoil.config_data)
        if not fetchrecipedir:
            logger.error('Searched BBFILES but unable to find a writeable place to put temporary recipe')
            sys.exit(1)
        fetchrecipe = None
        bb.utils.mkdirhier(fetchrecipedir)
        try:
            # Generate a dummy recipe so we can follow more or less normal paths
            # for do_fetch and do_unpack
            # I'd use tempfile functions here but underscores can be produced by that and those
            # aren't allowed in recipe file names except to separate the version
            rndstring = ''.join(random.choice(string.ascii_lowercase + string.digits) for _ in range(8))
            fetchrecipe = os.path.join(fetchrecipedir, 'tmp-recipetool-%s.bb' % rndstring)
            fetchrecipepn = os.path.splitext(os.path.basename(fetchrecipe))[0]
            logger.debug('Generating initial recipe %s for fetching' % fetchrecipe)
            with open(fetchrecipe, 'w') as f:
                # We don't want to have to specify LIC_FILES_CHKSUM
                f.write('LICENSE = "CLOSED"\n')
                # We don't need the cross-compiler
                f.write('INHIBIT_DEFAULT_DEPS = "1"\n')
                # We don't have the checksums yet so we can't require them
                f.write('BB_STRICT_CHECKSUM = "ignore"\n')
                f.write('SRC_URI = "%s"\n' % srcuri)
                f.write('SRCREV = "%s"\n' % srcrev)
                f.write('PV = "0.0+${SRCPV}"\n')
                f.write('WORKDIR = "%s"\n' % tmpworkdir)
                # Set S out of the way so it doesn't get created under the workdir
                f.write('S = "%s"\n' % os.path.join(tmpdir, 'emptysrc'))
                if not mirrors:
                    # We do not need PREMIRRORS since we are almost certainly
                    # fetching new source rather than something that has already
                    # been fetched. Hence, we disable them by default.
                    # However, we provide an option for users to enable it.
                    f.write('PREMIRRORS = ""\n')
                    f.write('MIRRORS = ""\n')

            logger.info('Fetching %s...' % srcuri)

            # FIXME this is too noisy at the moment

            # Parse recipes so our new recipe gets picked up
            tinfoil.parse_recipes()

            def eventhandler(event):
                if isinstance(event, bb.fetch2.MissingChecksumEvent):
                    checksums.update(event.checksums)
                    return True
                return False

            # Run the fetch + unpack tasks
            res = tinfoil.build_targets(fetchrecipepn,
                                        'do_unpack',
                                        handle_events=True,
                                        extra_events=['bb.fetch2.MissingChecksumEvent'],
                                        event_callback=eventhandler)
            if not res:
                raise FetchUrlFailure(srcuri)

            # Remove unneeded directories
            rd = tinfoil.parse_recipe(fetchrecipepn)
            if rd:
                pathvars = ['T', 'RECIPE_SYSROOT', 'RECIPE_SYSROOT_NATIVE']
                for pathvar in pathvars:
                    path = rd.getVar(pathvar)
                    if os.path.exists(path):
                        shutil.rmtree(path)
        finally:
            if fetchrecipe:
                try:
                    os.remove(fetchrecipe)
                except FileNotFoundError:
                    pass
            try:
                os.rmdir(fetchrecipedir)
            except OSError as e:
                import errno
                if e.errno != errno.ENOTEMPTY:
                    raise

        bb.utils.mkdirhier(destdir)
        for fn in os.listdir(tmpworkdir):
            shutil.move(os.path.join(tmpworkdir, fn), destdir)

    finally:
        if not preserve_tmp:
            shutil.rmtree(tmpdir)
            tmpdir = None

    return checksums, tmpdir


def run_editor(fn, logger=None):
    if isinstance(fn, str):
        files = [fn]
    else:
        files = fn

    editor = os.getenv('VISUAL', os.getenv('EDITOR', 'vi'))
    try:
        #print(shlex.split(editor) + files)
        return subprocess.check_call(shlex.split(editor) + files)
    except subprocess.CalledProcessError as exc:
        logger.error("Execution of '%s' failed: %s" % (editor, exc))
        return 1

def is_src_url(param):
    """
    Check if a parameter is a URL and return True if so
    NOTE: be careful about changing this as it will influence how devtool/recipetool command line handling works
    """
    if not param:
        return False
    elif '://' in param:
        return True
    elif param.startswith('git@') or ('@' in param and param.endswith('.git')):
        return True
    return False

def filter_src_subdirs(pth):
    """
    Filter out subdirectories of initial unpacked source trees that we do not care about.
    Used by devtool and recipetool.
    """
    dirlist = os.listdir(pth)
    filterout = ['git.indirectionsymlink', 'source-date-epoch']
    dirlist = [x for x in dirlist if x not in filterout]
    return dirlist
