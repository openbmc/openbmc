#!/usr/bin/env python3
#
# Copyright (c) 2017, Intel Corporation.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU General Public License,
# version 2, as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#

import os
import shutil
import csv
import sys
import argparse

__version__ = "0.1.0"

# set of BPNs
recipenames = set()
# map of recipe -> data
allrecipes = {}

def make_bpn(recipe):
    prefixes = ("nativesdk-",)
    suffixes = ("-native", "-cross", "-initial", "-intermediate", "-crosssdk", "-cross-canadian")
    for ix in prefixes + suffixes:
        if ix in recipe:
            recipe = recipe.replace(ix, "")
    return recipe

def gather_recipes(rows):
    for row in rows:
        recipe = row[0]
        bpn = make_bpn(recipe)
        if bpn not in recipenames:
            recipenames.add(bpn)
        if recipe not in allrecipes:
            allrecipes[recipe] = row

def generate_recipe_list():
    # machine list
    machine_list = ( "qemuarm64", "qemuarm", "qemumips64", "qemumips", "qemuppc", "qemux86-64", "qemux86" )
    # set filename format
    fnformat = 'distrodata.%s.csv'

    # store all data files in distrodata
    datadir = 'distrodata'

    # create the directory if it does not exists
    if not os.path.exists(datadir):
        os.mkdir(datadir)

    # doing bitbake distrodata
    for machine in machine_list:
        os.system('MACHINE='+ machine + ' bitbake -k universe -c distrodata')
        shutil.copy('tmp/log/distrodata.csv', 'distrodata/' + fnformat % machine)

    for machine in machine_list:
        with open('distrodata/' + fnformat % machine) as f:
            reader = csv.reader(f)
            rows = reader.__iter__()
            gather_recipes(rows)

    with open('recipe-list.txt', 'w') as f:
        for recipe in sorted(recipenames):
            f.write("%s\n" % recipe)
    print("file : recipe-list.txt is created with %d entries." % len(recipenames))

    with open('all-recipe-list.txt', 'w') as f:
        for recipe, row in sorted(allrecipes.items()):
            f.write("%s\n" % ','.join(row))


def diff_for_new_recipes(recipe1, recipe2):
    prev_recipe_path = recipe1 + '/'
    curr_recipe_path = recipe2 + '/'
    if not os.path.isfile(prev_recipe_path + 'recipe-list.txt') or not os.path.isfile(curr_recipe_path + 'recipe-list.txt'):
        print("recipe files do not exists. please verify that the file exists.")
        exit(1)

    import csv

    prev = []
    new = []

    with open(prev_recipe_path + 'recipe-list.txt') as f:
        prev = f.readlines()

    with open(curr_recipe_path + 'recipe-list.txt') as f:
        new = f.readlines()

    updates = []
    for pn in new:
        if not pn in prev:
            updates.append(pn.rstrip())

    allrecipe = []
    with open(recipe1 + '_' + recipe2 + '_new_recipe_list.txt','w') as dr:
        with open(curr_recipe_path + 'all-recipe-list.txt') as f:
            reader = csv.reader(f, delimiter=',')
            for row in reader:
                if row[0] in updates:
                    dr.write("%s,%s,%s" % (row[0], row[3], row[5]))
                    if len(row[9:]) > 0:
                        dr.write(",%s" % ','.join(row[9:]))
                    dr.write("\n")

def main(argv):
    if argv[0] == "generate_recipe_list":
        generate_recipe_list()
    elif argv[0] == "compare_recipe":
        diff_for_new_recipes(argv[1], argv[2])
    else:
        print("no such option. choose either 'generate_recipe_list' or 'compare_recipe'")

    exit(0)

if __name__ == "__main__":
    try:
        sys.exit(main(sys.argv[1:]))
    except Exception as e:
        print("Exception :", e)
        sys.exit(1)

