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

def logger_create(name):
    logger = logging.getLogger(name)
    loggerhandler = logging.StreamHandler()
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

    logger.debug('Loading plugins from %s...' % pluginpath)
    for fn in glob.glob(os.path.join(pluginpath, '*.py')):
        name = os.path.splitext(os.path.basename(fn))[0]
        if name != '__init__':
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

def fetch_uri(d, uri, destdir, srcrev=None):
    """Fetch a URI to a local directory"""
    import bb.data
    bb.utils.mkdirhier(destdir)
    localdata = bb.data.createCopy(d)
    localdata.setVar('BB_STRICT_CHECKSUM', '')
    localdata.setVar('SRCREV', srcrev)
    ret = (None, None)
    olddir = os.getcwd()
    try:
        fetcher = bb.fetch2.Fetch([uri], localdata)
        for u in fetcher.ud:
            ud = fetcher.ud[u]
            ud.ignore_checksums = True
        fetcher.download()
        for u in fetcher.ud:
            ud = fetcher.ud[u]
            if ud.localpath.rstrip(os.sep) == localdata.getVar('DL_DIR', True).rstrip(os.sep):
                raise Exception('Local path is download directory - please check that the URI "%s" is correct' % uri)
        fetcher.unpack(destdir)
        for u in fetcher.ud:
            ud = fetcher.ud[u]
            if ud.method.recommends_checksum(ud):
                md5value = bb.utils.md5_file(ud.localpath)
                sha256value = bb.utils.sha256_file(ud.localpath)
                ret = (md5value, sha256value)
    finally:
        os.chdir(olddir)
    return ret

def run_editor(fn):
    if isinstance(fn, basestring):
        params = '"%s"' % fn
    else:
        params = ''
        for fnitem in fn:
            params += ' "%s"' % fnitem

    editor = os.getenv('VISUAL', os.getenv('EDITOR', 'vi'))
    try:
        return subprocess.check_call('%s %s' % (editor, params), shell=True)
    except OSError as exc:
        logger.error("Execution of editor '%s' failed: %s", editor, exc)
        return 1
