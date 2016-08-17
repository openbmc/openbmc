#!/usr/bin/env python
# ex:ts=4:sw=4:sts=4:et
# -*- tab-width: 4; c-basic-offset: 4; indent-tabs-mode: nil -*-
"""
   class for handling configuration data files

   Reads a .conf file and obtains its metadata

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

import errno
import re
import os
import bb.utils
from bb.parse import ParseError, resolve_file, ast, logger, handle

__config_regexp__  = re.compile( r"""
    ^
    (?P<exp>export\s*)?
    (?P<var>[a-zA-Z0-9\-~_+.${}/]+?)
    (\[(?P<flag>[a-zA-Z0-9\-_+.]+)\])?

    \s* (
        (?P<colon>:=) |
        (?P<lazyques>\?\?=) |
        (?P<ques>\?=) |
        (?P<append>\+=) |
        (?P<prepend>=\+) |
        (?P<predot>=\.) |
        (?P<postdot>\.=) |
        =
    ) \s*

    (?!'[^']*'[^']*'$)
    (?!\"[^\"]*\"[^\"]*\"$)
    (?P<apo>['\"])
    (?P<value>.*)
    (?P=apo)
    $
    """, re.X)
__include_regexp__ = re.compile( r"include\s+(.+)" )
__require_regexp__ = re.compile( r"require\s+(.+)" )
__export_regexp__ = re.compile( r"export\s+([a-zA-Z0-9\-_+.${}/]+)$" )

def init(data):
    topdir = data.getVar('TOPDIR', False)
    if not topdir:
        data.setVar('TOPDIR', os.getcwd())


def supports(fn, d):
    return fn[-5:] == ".conf"

def include(parentfn, fn, lineno, data, error_out):
    """
    error_out: A string indicating the verb (e.g. "include", "inherit") to be
    used in a ParseError that will be raised if the file to be included could
    not be included. Specify False to avoid raising an error in this case.
    """
    if parentfn == fn: # prevent infinite recursion
        return None

    fn = data.expand(fn)
    parentfn = data.expand(parentfn)

    if not os.path.isabs(fn):
        dname = os.path.dirname(parentfn)
        bbpath = "%s:%s" % (dname, data.getVar("BBPATH", True))
        abs_fn, attempts = bb.utils.which(bbpath, fn, history=True)
        if abs_fn and bb.parse.check_dependency(data, abs_fn):
            logger.warn("Duplicate inclusion for %s in %s" % (abs_fn, data.getVar('FILE', True)))
        for af in attempts:
            bb.parse.mark_dependency(data, af)
        if abs_fn:
            fn = abs_fn
    elif bb.parse.check_dependency(data, fn):
        logger.warn("Duplicate inclusion for %s in %s" % (fn, data.getVar('FILE', True)))

    try:
        bb.parse.handle(fn, data, True)
    except (IOError, OSError) as exc:
        if exc.errno == errno.ENOENT:
            if error_out:
                raise ParseError("Could not %s file %s" % (error_out, fn), parentfn, lineno)
            logger.debug(2, "CONF file '%s' not found", fn)
        else:
            if error_out:
                raise ParseError("Could not %s file %s: %s" % (error_out, fn, exc.strerror), parentfn, lineno)
            else:
                raise ParseError("Error parsing %s: %s" % (fn, exc.strerror), parentfn, lineno)

# We have an issue where a UI might want to enforce particular settings such as
# an empty DISTRO variable. If configuration files do something like assigning
# a weak default, it turns out to be very difficult to filter out these changes,
# particularly when the weak default might appear half way though parsing a chain 
# of configuration files. We therefore let the UIs hook into configuration file
# parsing. This turns out to be a hard problem to solve any other way.
confFilters = []

def handle(fn, data, include):
    init(data)

    if include == 0:
        oldfile = None
    else:
        oldfile = data.getVar('FILE', False)

    abs_fn = resolve_file(fn, data)
    f = open(abs_fn, 'r')

    if include:
        bb.parse.mark_dependency(data, abs_fn)

    statements = ast.StatementGroup()
    lineno = 0
    while True:
        lineno = lineno + 1
        s = f.readline()
        if not s:
            break
        w = s.strip()
        # skip empty lines
        if not w:
            continue
        s = s.rstrip()
        while s[-1] == '\\':
            s2 = f.readline().strip()
            lineno = lineno + 1
            if (not s2 or s2 and s2[0] != "#") and s[0] == "#" :
                bb.fatal("There is a confusing multiline, partially commented expression on line %s of file %s (%s).\nPlease clarify whether this is all a comment or should be parsed." % (lineno, fn, s))
            s = s[:-1] + s2
        # skip comments
        if s[0] == '#':
            continue
        feeder(lineno, s, abs_fn, statements)

    # DONE WITH PARSING... time to evaluate
    data.setVar('FILE', abs_fn)
    statements.eval(data)
    if oldfile:
        data.setVar('FILE', oldfile)

    f.close()

    for f in confFilters:
        f(fn, data)

    return data

def feeder(lineno, s, fn, statements):
    m = __config_regexp__.match(s)
    if m:
        groupd = m.groupdict()
        ast.handleData(statements, fn, lineno, groupd)
        return

    m = __include_regexp__.match(s)
    if m:
        ast.handleInclude(statements, fn, lineno, m, False)
        return

    m = __require_regexp__.match(s)
    if m:
        ast.handleInclude(statements, fn, lineno, m, True)
        return

    m = __export_regexp__.match(s)
    if m:
        ast.handleExport(statements, fn, lineno, m)
        return

    raise ParseError("unparsed line: '%s'" % s, fn, lineno);

# Add us to the handlers list
from bb.parse import handlers
handlers.append({'supports': supports, 'handle': handle, 'init': init})
del handlers
