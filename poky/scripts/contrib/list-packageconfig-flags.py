#!/usr/bin/env python3

# Copyright (C) 2013 Wind River Systems, Inc.
# Copyright (C) 2014 Intel Corporation
#
# SPDX-License-Identifier: GPL-2.0-or-later
#
# - list available recipes which have PACKAGECONFIG flags
# - list available PACKAGECONFIG flags and all affected recipes
# - list all recipes and PACKAGECONFIG information

import sys
import optparse
import os


scripts_path = os.path.abspath(os.path.dirname(os.path.abspath(sys.argv[0])))
lib_path = os.path.abspath(scripts_path + '/../lib')
sys.path = sys.path + [lib_path]

import scriptpath

# For importing the following modules
bitbakepath = scriptpath.add_bitbake_lib_path()
if not bitbakepath:
    sys.stderr.write("Unable to find bitbake by searching parent directory of this script or PATH\n")
    sys.exit(1)

import bb.cooker
import bb.providers
import bb.tinfoil

def get_fnlist(bbhandler, pkg_pn, preferred):
    ''' Get all recipe file names '''
    if preferred:
        (latest_versions, preferred_versions) = bb.providers.findProviders(bbhandler.config_data, bbhandler.cooker.recipecaches[''], pkg_pn)

    fn_list = []
    for pn in sorted(pkg_pn):
        if preferred:
            fn_list.append(preferred_versions[pn][1])
        else:
            fn_list.extend(pkg_pn[pn])

    return fn_list

def get_recipesdata(bbhandler, preferred):
    ''' Get data of all available recipes which have PACKAGECONFIG flags '''
    pkg_pn = bbhandler.cooker.recipecaches[''].pkg_pn

    data_dict = {}
    for fn in get_fnlist(bbhandler, pkg_pn, preferred):
        data = bbhandler.parse_recipe_file(fn)
        flags = data.getVarFlags("PACKAGECONFIG")
        flags.pop('doc', None)
        if flags:
            data_dict[fn] = data

    return data_dict

def collect_pkgs(data_dict):
    ''' Collect available pkgs in which have PACKAGECONFIG flags '''
    # pkg_dict = {'pkg1': ['flag1', 'flag2',...]}
    pkg_dict = {}
    for fn in data_dict:
        pkgconfigflags = data_dict[fn].getVarFlags("PACKAGECONFIG")
        pkgconfigflags.pop('doc', None)
        pkgname = data_dict[fn].getVar("PN")
        pkg_dict[pkgname] = sorted(pkgconfigflags.keys())

    return pkg_dict

def collect_flags(pkg_dict):
    ''' Collect available PACKAGECONFIG flags and all affected pkgs '''
    # flag_dict = {'flag': ['pkg1', 'pkg2',...]}
    flag_dict = {}
    for pkgname, flaglist in pkg_dict.items():
        for flag in flaglist:
            if flag in flag_dict:
                flag_dict[flag].append(pkgname)
            else:
                flag_dict[flag] = [pkgname]

    return flag_dict

def display_pkgs(pkg_dict):
    ''' Display available pkgs which have PACKAGECONFIG flags '''
    pkgname_len = len("RECIPE NAME") + 1
    for pkgname in pkg_dict:
        if pkgname_len < len(pkgname):
            pkgname_len = len(pkgname)
    pkgname_len += 1

    header = '%-*s%s' % (pkgname_len, str("RECIPE NAME"), str("PACKAGECONFIG FLAGS"))
    print(header)
    print(str("").ljust(len(header), '='))
    for pkgname in sorted(pkg_dict):
        print('%-*s%s' % (pkgname_len, pkgname, ' '.join(pkg_dict[pkgname])))


def display_flags(flag_dict):
    ''' Display available PACKAGECONFIG flags and all affected pkgs '''
    flag_len = len("PACKAGECONFIG FLAG") + 5

    header = '%-*s%s' % (flag_len, str("PACKAGECONFIG FLAG"), str("RECIPE NAMES"))
    print(header)
    print(str("").ljust(len(header), '='))

    for flag in sorted(flag_dict):
        print('%-*s%s' % (flag_len, flag, '  '.join(sorted(flag_dict[flag]))))

def display_all(data_dict):
    ''' Display all pkgs and PACKAGECONFIG information '''
    print(str("").ljust(50, '='))
    for fn in data_dict:
        print('%s' % data_dict[fn].getVar("P"))
        print(fn)
        packageconfig = data_dict[fn].getVar("PACKAGECONFIG") or ''
        if packageconfig.strip() == '':
            packageconfig = 'None'
        print('PACKAGECONFIG %s' % packageconfig)

        for flag,flag_val in data_dict[fn].getVarFlags("PACKAGECONFIG").items():
            if flag == "doc":
                continue
            print('PACKAGECONFIG[%s] %s' % (flag, flag_val))
        print('')

def main():
    pkg_dict = {}
    flag_dict = {}

    # Collect and validate input
    parser = optparse.OptionParser(
        description = "Lists recipes and PACKAGECONFIG flags. Without -a or -f, recipes and their available PACKAGECONFIG flags are listed.",
        usage = """
    %prog [options]""")

    parser.add_option("-f", "--flags",
            help = "list available PACKAGECONFIG flags and affected recipes",
            action="store_const", dest="listtype", const="flags", default="recipes")
    parser.add_option("-a", "--all",
            help = "list all recipes and PACKAGECONFIG information",
            action="store_const", dest="listtype", const="all")
    parser.add_option("-p", "--preferred-only",
            help = "where multiple recipe versions are available, list only the preferred version",
            action="store_true", dest="preferred", default=False)

    options, args = parser.parse_args(sys.argv)

    with bb.tinfoil.Tinfoil() as bbhandler:
        bbhandler.prepare()
        print("Gathering recipe data...")
        data_dict = get_recipesdata(bbhandler, options.preferred)

        if options.listtype == 'flags':
            pkg_dict = collect_pkgs(data_dict)
            flag_dict = collect_flags(pkg_dict)
            display_flags(flag_dict)
        elif options.listtype == 'recipes':
            pkg_dict = collect_pkgs(data_dict)
            display_pkgs(pkg_dict)
        elif options.listtype == 'all':
            display_all(data_dict)

if __name__ == "__main__":
    main()
