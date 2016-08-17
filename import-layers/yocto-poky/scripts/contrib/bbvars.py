#!/usr/bin/env python

# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
# Copyright (C) Darren Hart <dvhart@linux.intel.com>, 2010


import sys
import getopt
import os
import os.path
import re

def usage():
    print 'Usage: %s -d FILENAME [-d FILENAME]* -m METADIR [-m MATADIR]*' % os.path.basename(sys.argv[0])
    print '  -d FILENAME         documentation file to search'
    print '  -h, --help          display this help and exit'
    print '  -m METADIR          meta directory to search for recipes'
    print '  -t FILENAME         documentation config file (for doc tags)'
    print '  -T                  Only display variables with doc tags (requires -t)'

def recipe_bbvars(recipe):
    ''' Return a unique set of every bbvar encountered in the recipe '''
    prog = re.compile("[A-Z_]+")
    vset = set()
    try:
        r = open(recipe)
    except IOError as (errno, strerror):
        print 'WARNING: Failed to open recipe ', recipe
        print strerror

    for line in r:
        # Strip any comments from the line
        line = line.rsplit('#')[0]
        vset = vset.union(set(prog.findall(line)))
    r.close()

    bbvars = {}
    for v in vset:
        bbvars[v] = 1

    return bbvars

def collect_bbvars(metadir):
    ''' Walk the metadir and collect the bbvars from each recipe found '''
    bbvars = {}
    for root,dirs,files in os.walk(metadir):
        for name in files:
            if name.find(".bb") >= 0:
                for key in recipe_bbvars(os.path.join(root,name)).iterkeys():
                    if bbvars.has_key(key):
                        bbvars[key] = bbvars[key] + 1
                    else:
                        bbvars[key] = 1
    return bbvars

def bbvar_is_documented(var, docfiles):
    prog = re.compile(".*($|[^A-Z_])%s([^A-Z_]|$)" % (var))
    for doc in docfiles:
        try:
            f = open(doc)
        except IOError as (errno, strerror):
            print 'WARNING: Failed to open doc ', doc
            print strerror
        for line in f:
            if prog.match(line):
                return True
        f.close()
    return False

def bbvar_doctag(var, docconf):
    prog = re.compile('^%s\[doc\] *= *"(.*)"' % (var))
    if docconf == "":
        return "?"

    try:
        f = open(docconf)
    except IOError as (errno, strerror):
        return strerror

    for line in f:
        m = prog.search(line)
        if m:
            return m.group(1)

    f.close()
    return ""

def main():
    docfiles = []
    metadirs = []
    bbvars = {}
    undocumented = []
    docconf = ""
    onlydoctags = False

    # Collect and validate input
    try:
        opts, args = getopt.getopt(sys.argv[1:], "d:hm:t:T", ["help"])
    except getopt.GetoptError, err:
        print '%s' % str(err)
        usage()
        sys.exit(2)

    for o, a in opts:
        if o in ('-h', '--help'):
            usage()
            sys.exit(0)
        elif o == '-d':
            if os.path.isfile(a):
                docfiles.append(a)
            else:
                print 'ERROR: documentation file %s is not a regular file' % (a)
                sys.exit(3)
        elif o == '-m':
            if os.path.isdir(a):
                metadirs.append(a)
            else:
                print 'ERROR: meta directory %s is not a directory' % (a)
                sys.exit(4)
        elif o == "-t":
            if os.path.isfile(a):
                docconf = a
        elif o == "-T":
            onlydoctags = True
        else:
            assert False, "unhandled option"

    if len(docfiles) == 0:
        print 'ERROR: no docfile specified'
        usage()
        sys.exit(5)

    if len(metadirs) == 0:
        print 'ERROR: no metadir specified'
        usage()
        sys.exit(6)

    if onlydoctags and docconf == "":
        print 'ERROR: no docconf specified'
        usage()
        sys.exit(7)

    # Collect all the variable names from the recipes in the metadirs
    for m in metadirs:
        for key,cnt in collect_bbvars(m).iteritems():
            if bbvars.has_key(key):
                bbvars[key] = bbvars[key] + cnt
            else:
                bbvars[key] = cnt

    # Check each var for documentation
    varlen = 0
    for v in bbvars.iterkeys():
        if len(v) > varlen:
            varlen = len(v)
        if not bbvar_is_documented(v, docfiles):
            undocumented.append(v)
    undocumented.sort()
    varlen = varlen + 1

    # Report all undocumented variables
    print 'Found %d undocumented bb variables (out of %d):' % (len(undocumented), len(bbvars))
    header = '%s%s%s' % (str("VARIABLE").ljust(varlen), str("COUNT").ljust(6), str("DOCTAG").ljust(7))
    print header
    print str("").ljust(len(header), '=')
    for v in undocumented:
        doctag = bbvar_doctag(v, docconf)
        if not onlydoctags or not doctag == "":
            print '%s%s%s' % (v.ljust(varlen), str(bbvars[v]).ljust(6), doctag)


if __name__ == "__main__":
    main()
