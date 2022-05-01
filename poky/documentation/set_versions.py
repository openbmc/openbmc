#!/usr/bin/env python3
#
# Add version information to poky.yaml based upon current git branch/tags
#
# Copyright Linux Foundation
# Author: Richard Purdie <richard.purdie@linuxfoundation.org>
#
# SPDX-License-Identifier: MIT
#


import subprocess
import collections
import sys
import os
import itertools

ourversion = None
if len(sys.argv) == 2:
    ourversion = sys.argv[1]

ourversion = None
if len(sys.argv) == 2:
    ourversion = sys.argv[1]

activereleases = ["kirkstone", "honister", "hardknott", "dunfell"]
devbranch = "langdale"
ltsseries = ["kirkstone", "dunfell"]

release_series = collections.OrderedDict()
release_series["langdale"] = "4.1"
release_series["kirkstone"] = "4.0"
release_series["honister"] = "3.4"
release_series["hardknott"] = "3.3"
release_series["gatesgarth"] = "3.2"
release_series["dunfell"] = "3.1"
release_series["zeus"] = "3.0"
release_series["warrior"] = "2.7"
release_series["thud"] = "2.6"
release_series["sumo"] = "2.5"
release_series["rocko"] = "2.4"
release_series["pyro"] = "2.3"
release_series["morty"] = "2.2"
release_series["krogoth"] = "2.1"
release_series["jethro"] = "2.0"
release_series["jethro-pre"] = "1.9"
release_series["fido"] = "1.8"
release_series["dizzy"] = "1.7"
release_series["daisy"] = "1.6"
release_series["dora"] = "1.5"
release_series["dylan"] = "1.4"
release_series["danny"] = "1.3"
release_series["denzil"] = "1.2"
release_series["edison"] = "1.1"
release_series["bernard"] = "1.0"
release_series["laverne"] = "0.9"


bitbake_mapping = {
    "langdale" : "2.2",
    "kirkstone" : "2.0",
    "honister" : "1.52",
    "hardknott" : "1.50",
    "gatesgarth" : "1.48",
    "dunfell" : "1.46",
}

# 3.4 onwards doesn't have poky version
# Early 3.4 release docs do reference it though
poky_mapping = {
    "3.4" : "26.0",
    "3.3" : "25.0",
    "3.2" : "24.0",
    "3.1" : "23.0",
}

ourseries = None
ourbranch = None
bitbakeversion = None
docconfver = None

# Test tags exist and inform the user to fetch if not
try:
    subprocess.run(["git", "show", "yocto-3.4.2"], capture_output=True, check=True)
except subprocess.CalledProcessError:
    sys.exit("Please run 'git fetch --tags' before building the documentation")

# Try and figure out what we are
tags = subprocess.run(["git", "tag", "--points-at", "HEAD"], capture_output=True, text=True).stdout
for t in tags.split():
    if t.startswith("yocto-"):
        ourversion = t[6:]

if ourversion:
    # We're a tagged release
    components = ourversion.split(".")
    baseversion = components[0] + "." + components[1]
    docconfver = ourversion
    for i in release_series:
        if release_series[i] == baseversion:
            ourseries = i
            ourbranch = i
            if i in bitbake_mapping:
                bitbakeversion = bitbake_mapping[i]
else:
    # We're floating on a branch
    branch = subprocess.run(["git", "branch", "--show-current"], capture_output=True, text=True).stdout.strip()
    ourbranch = branch
    if branch != "master" and branch not in release_series:
        # We're not on a known release branch so we have to guess. Compare the numbers of commits
        # from each release branch and assume the smallest number of commits is the one we're based off
        possible_branch = None
        branch_count = 0
        for b in itertools.chain(release_series.keys(), ["master"]):
            result = subprocess.run(["git", "log", "--format=oneline",  "HEAD..origin/" + b], capture_output=True, text=True)
            if result.returncode == 0:
                count = result.stdout.count('\n')
                if not possible_branch or count < branch_count:
                    print("Branch %s has count %s" % (b, count))
                    possible_branch = b
                    branch_count = count
        if possible_branch:
            branch = possible_branch
        else:
            branch = "master"
        print("Nearest release branch estimated to be %s" % branch)
    if branch == "master":
        ourseries = devbranch
        docconfver = "dev"
        bitbakeversion = "dev"
    elif branch in release_series:
        ourseries = branch
        if branch in bitbake_mapping:
            bitbakeversion = bitbake_mapping[branch]
    else:
        sys.exit("Unknown series for branch %s" % branch)

    previoustags = subprocess.run(["git", "tag", "--merged", "HEAD"], capture_output=True, text=True).stdout
    previoustags = [t[6:] for t in previoustags.split() if t.startswith("yocto-" + release_series[ourseries])]
    futuretags = subprocess.run(["git", "tag", "--merged", ourbranch], capture_output=True, text=True).stdout
    futuretags = [t[6:] for t in futuretags.split() if t.startswith("yocto-" + release_series[ourseries])]

    # Append .999 against the last known version
    if len(previoustags) != len(futuretags):
        ourversion = previoustags[-1] + ".999"
    else:
        ourversion = release_series[ourseries] + ".999"
    if not docconfver:
        docconfver = ourversion

series = [k for k in release_series]
previousseries = series[series.index(ourseries)+1:] or [""]
lastlts = [k for k in previousseries if k in ltsseries] or "dunfell"

print("Version calculated to be %s" % ourversion)
print("Release series calculated to be %s" % ourseries)

replacements = {
    "DISTRO" : ourversion,
    "DISTRO_NAME_NO_CAP" : ourseries,
    "DISTRO_NAME" : ourseries.capitalize(),
    "DISTRO_NAME_NO_CAP_MINUS_ONE" : previousseries[0],
    "DISTRO_NAME_NO_CAP_LTS" : lastlts[0],
    "YOCTO_DOC_VERSION" : ourversion,
    "DISTRO_REL_TAG" : "yocto-" + ourversion,
    "DOCCONF_VERSION" : docconfver,
    "BITBAKE_SERIES" : bitbakeversion,
}

if release_series[ourseries] in poky_mapping:
    pokyversion = poky_mapping[release_series[ourseries]]
    if ourversion != release_series[ourseries]:
        pokyversion = pokyversion + "." + ourversion.rsplit(".", 1)[1]
    else:
        pokyversion = pokyversion + ".0"
    replacements["POKYVERSION"] = pokyversion

if os.path.exists("poky.yaml.in"):
    with open("poky.yaml.in", "r") as r, open("poky.yaml", "w") as w:
        lines = r.readlines()
        for line in lines:
            data = line.split(":")
            k = data[0].strip()
            if k in replacements:
                w.write("%s : \"%s\"\n" % (k, replacements[k]))
            else:
                w.write(line)

    print("poky.yaml generated from poky.yaml.in")


# In the switcher list of versions we display:
#  - latest dev
#  - latest stable release
#  - latest LTS
#  - latest for each releases listed as active
#  - latest doc version in current series
#  - current doc version
# (with duplicates removed)

versions = []
with open("sphinx-static/switchers.js.in", "r") as r, open("sphinx-static/switchers.js", "w") as w:
    lines = r.readlines()
    for line in lines:
        if "ALL_RELEASES_PLACEHOLDER" in line:
            w.write(str(list(release_series.keys())))
            continue
        if "VERSIONS_PLACEHOLDER" in line:
            w.write("    'dev': { 'title': 'dev (%s)', 'obsolete': false,},\n" % release_series[devbranch])
            for branch in activereleases + ([ourseries] if ourseries not in activereleases else []):
                if branch == devbranch:
                    continue
                branch_versions = subprocess.run('git tag --list yocto-%s*' % (release_series[branch]), shell=True, capture_output=True, text=True).stdout.split()
                branch_versions = sorted([v.replace("yocto-" +  release_series[branch] + ".", "").replace("yocto-" +  release_series[branch], "0") for v in branch_versions], key=int)
                if not branch_versions:
                    continue
                version = release_series[branch]
                if branch_versions[-1] != "0":
                    version = version + "." + branch_versions[-1]
                versions.append(version)
                w.write("    '%s': {'title': '%s', 'obsolete': %s,},\n" % (version, version, str(branch not in activereleases).lower()))
            if ourversion not in versions and ourseries != devbranch:
                w.write("    '%s': {'title': '%s', 'obsolete': %s,},\n" % (ourversion, ourversion, str(ourseries not in activereleases).lower()))
        else:
            w.write(line)

print("switchers.js generated from switchers.js.in")

