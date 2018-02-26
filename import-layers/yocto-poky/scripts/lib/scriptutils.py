# Script utility functions
#
# Copyright (C) 2014 Intel Corporation
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as
# published by the Free Software Foundation.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import sys
import os
import logging
import glob
import argparse
import subprocess
import tempfile
import shutil
import random
import string

def logger_create(name, stream=None):
    logger = logging.getLogger(name)
    loggerhandler = logging.StreamHandler(stream=stream)
    loggerhandler.setFormatter(logging.Formatter("%(levelname)s: %(message)s"))
    logger.addHandler(loggerhandler)
    logger.setLevel(logging.INFO)
    return logger

def logger_setup_color(logger, color='auto'):
    from bb.msg import BBLogFormatter
    console = logging.StreamHandler(sys.stdout)
    formatter = BBLogFormatter("%(levelname)s: %(message)s")
    console.setFormatter(formatter)
    logger.handlers = [console]
    if color == 'always' or (color=='auto' and console.stream.isatty()):
        formatter.enable_color()


def load_plugins(logger, plugins, pluginpath):
    import imp

    def load_plugin(name):
        logger.debug('Loading plugin %s' % name)
        fp, pathname, description = imp.find_module(name, [pluginpath])
        try:
            return imp.load_module(name, fp, pathname, description)
        finally:
            if fp:
                fp.close()

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
        params = '"%s"' % fn
    else:
        params = ''
        for fnitem in fn:
            params += ' "%s"' % fnitem

    editor = os.getenv('VISUAL', os.getenv('EDITOR', 'vi'))
    try:
        return subprocess.check_call('%s %s' % (editor, params), shell=True)
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
