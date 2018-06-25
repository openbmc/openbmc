#!/usr/bin/env python3

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

# Set up sys.path to let us import tinfoil
scripts_path = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))
lib_path = scripts_path + '/lib'
sys.path.insert(0, lib_path)
import scriptpath
scriptpath.add_bitbake_lib_path()
import bb.tinfoil

def usage():
    print('Usage: %s -d FILENAME [-d FILENAME]*' % os.path.basename(sys.argv[0]))
    print('  -d FILENAME         documentation file to search')
    print('  -h, --help          display this help and exit')
    print('  -t FILENAME         documentation config file (for doc tags)')
    print('  -T                  Only display variables with doc tags (requires -t)')

def bbvar_is_documented(var, documented_vars):
    ''' Check if variable (var) is in the list of documented variables(documented_vars) '''
    if var in documented_vars:
        return True
    else:
        return False

def collect_documented_vars(docfiles):
    ''' Walk the docfiles and collect the documented variables '''
    documented_vars = []
    prog = re.compile(".*($|[^A-Z_])<glossentry id=\'var-")
    var_prog = re.compile('<glossentry id=\'var-(.*)\'>')
    for d in docfiles:
        with open(d) as f:
            documented_vars += var_prog.findall(f.read())

    return documented_vars

def bbvar_doctag(var, docconf):
    prog = re.compile('^%s\[doc\] *= *"(.*)"' % (var))
    if docconf == "":
        return "?"

    try:
        f = open(docconf)
    except IOError as err:
        return err.args[1]

    for line in f:
        m = prog.search(line)
        if m:
            return m.group(1)

    f.close()
    return ""

def main():
    docfiles = []
    bbvars = set()
    undocumented = []
    docconf = ""
    onlydoctags = False

    # Collect and validate input
    try:
        opts, args = getopt.getopt(sys.argv[1:], "d:hm:t:T", ["help"])
    except getopt.GetoptError as err:
        print('%s' % str(err))
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
                print('ERROR: documentation file %s is not a regular file' % a)
                sys.exit(3)
        elif o == "-t":
            if os.path.isfile(a):
                docconf = a
        elif o == "-T":
            onlydoctags = True
        else:
            assert False, "unhandled option"

    if len(docfiles) == 0:
        print('ERROR: no docfile specified')
        usage()
        sys.exit(5)

    if onlydoctags and docconf == "":
        print('ERROR: no docconf specified')
        usage()
        sys.exit(7)

    prog = re.compile("^[^a-z]*$")
    with bb.tinfoil.Tinfoil() as tinfoil:
        tinfoil.prepare(config_only=False)
        parser = bb.codeparser.PythonParser('parser', None)
        datastore = tinfoil.config_data

        def bbvars_update(data):
            if prog.match(data):
                bbvars.add(data)
            if tinfoil.config_data.getVarFlag(data, 'python'):
                try:
                    parser.parse_python(tinfoil.config_data.getVar(data))
                except bb.data_smart.ExpansionError:
                    pass
                for var in parser.references:
                    if prog.match(var):
                        bbvars.add(var)
            else:
                try:
                    expandedVar = datastore.expandWithRefs(datastore.getVar(data, False), data)
                    for var in expandedVar.references:
                        if prog.match(var):
                            bbvars.add(var)
                except bb.data_smart.ExpansionError:
                    pass

        # Use tinfoil to collect all the variable names globally
        for data in datastore:
            bbvars_update(data)

        # Collect variables from all recipes
        for recipe in tinfoil.all_recipe_files(variants=False):
            print("Checking %s" % recipe)
            for data in tinfoil.parse_recipe_file(recipe):
                bbvars_update(data)

    documented_vars = collect_documented_vars(docfiles)

    # Check each var for documentation
    varlen = 0
    for v in bbvars:
        if len(v) > varlen:
            varlen = len(v)
        if not bbvar_is_documented(v, documented_vars):
            undocumented.append(v)
    undocumented.sort()
    varlen = varlen + 1

    # Report all undocumented variables
    print('Found %d undocumented bb variables (out of %d):' % (len(undocumented), len(bbvars)))
    header = '%s%s' % (str("VARIABLE").ljust(varlen), str("DOCTAG").ljust(7))
    print(header)
    print(str("").ljust(len(header), '='))
    for v in undocumented:
        doctag = bbvar_doctag(v, docconf)
        if not onlydoctags or not doctag == "":
            print('%s%s' % (v.ljust(varlen), doctag))


if __name__ == "__main__":
    main()
