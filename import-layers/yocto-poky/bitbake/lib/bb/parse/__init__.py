"""
BitBake Parsers

File parsers for the BitBake build tools.

"""


# Copyright (C) 2003, 2004  Chris Larson
# Copyright (C) 2003, 2004  Phil Blundell
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
#
# Based on functions from the base bb module, Copyright 2003 Holger Schurig

handlers = []

import errno
import logging
import os
import stat
import bb
import bb.utils
import bb.siggen

logger = logging.getLogger("BitBake.Parsing")

class ParseError(Exception):
    """Exception raised when parsing fails"""
    def __init__(self, msg, filename, lineno=0):
        self.msg = msg
        self.filename = filename
        self.lineno = lineno
        Exception.__init__(self, msg, filename, lineno)

    def __str__(self):
        if self.lineno:
            return "ParseError at %s:%d: %s" % (self.filename, self.lineno, self.msg)
        else:
            return "ParseError in %s: %s" % (self.filename, self.msg)

class SkipRecipe(Exception):
    """Exception raised to skip this recipe"""

class SkipPackage(SkipRecipe):
    """Exception raised to skip this recipe (use SkipRecipe in new code)"""

__mtime_cache = {}
def cached_mtime(f):
    if f not in __mtime_cache:
        __mtime_cache[f] = os.stat(f)[stat.ST_MTIME]
    return __mtime_cache[f]

def cached_mtime_noerror(f):
    if f not in __mtime_cache:
        try:
            __mtime_cache[f] = os.stat(f)[stat.ST_MTIME]
        except OSError:
            return 0
    return __mtime_cache[f]

def update_mtime(f):
    try:
        __mtime_cache[f] = os.stat(f)[stat.ST_MTIME]
    except OSError:
        if f in __mtime_cache:
            del __mtime_cache[f]
        return 0
    return __mtime_cache[f]

def update_cache(f):
    if f in __mtime_cache:
        logger.debug(1, "Updating mtime cache for %s" % f)
        update_mtime(f)

def mark_dependency(d, f):
    if f.startswith('./'):
        f = "%s/%s" % (os.getcwd(), f[2:])
    deps = (d.getVar('__depends', False) or [])
    s = (f, cached_mtime_noerror(f))
    if s not in deps:
        deps.append(s)
        d.setVar('__depends', deps)

def check_dependency(d, f):
    s = (f, cached_mtime_noerror(f))
    deps = (d.getVar('__depends', False) or [])
    return s in deps
   
def supports(fn, data):
    """Returns true if we have a handler for this file, false otherwise"""
    for h in handlers:
        if h['supports'](fn, data):
            return 1
    return 0

def handle(fn, data, include = 0):
    """Call the handler that is appropriate for this file"""
    for h in handlers:
        if h['supports'](fn, data):
            with data.inchistory.include(fn):
                return h['handle'](fn, data, include)
    raise ParseError("not a BitBake file", fn)

def init(fn, data):
    for h in handlers:
        if h['supports'](fn):
            return h['init'](data)

def init_parser(d):
    bb.parse.siggen = bb.siggen.init(d)

def resolve_file(fn, d):
    if not os.path.isabs(fn):
        bbpath = d.getVar("BBPATH", True)
        newfn, attempts = bb.utils.which(bbpath, fn, history=True)
        for af in attempts:
            mark_dependency(d, af)
        if not newfn:
            raise IOError(errno.ENOENT, "file %s not found in %s" % (fn, bbpath))
        fn = newfn

    mark_dependency(d, fn)
    if not os.path.isfile(fn):
        raise IOError(errno.ENOENT, "file %s not found" % fn)

    return fn

# Used by OpenEmbedded metadata
__pkgsplit_cache__={}
def vars_from_file(mypkg, d):
    if not mypkg or not mypkg.endswith((".bb", ".bbappend")):
        return (None, None, None)
    if mypkg in __pkgsplit_cache__:
        return __pkgsplit_cache__[mypkg]

    myfile = os.path.splitext(os.path.basename(mypkg))
    parts = myfile[0].split('_')
    __pkgsplit_cache__[mypkg] = parts
    if len(parts) > 3:
        raise ParseError("Unable to generate default variables from filename (too many underscores)", mypkg)
    exp = 3 - len(parts)
    tmplist = []
    while exp != 0:
        exp -= 1
        tmplist.append(None)
    parts.extend(tmplist)
    return parts

def get_file_depends(d):
    '''Return the dependent files'''
    dep_files = []
    depends = d.getVar('__base_depends', False) or []
    depends = depends + (d.getVar('__depends', False) or [])
    for (fn, _) in depends:
        dep_files.append(os.path.abspath(fn))
    return " ".join(dep_files)

from bb.parse.parse_py import __version__, ConfHandler, BBHandler
