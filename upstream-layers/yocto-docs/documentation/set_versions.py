#!/usr/bin/env python3
#
# Add version information to poky.yaml based upon current git branch/tags
# Also generate the list of available manuals (releases.rst file)
#
# Copyright Linux Foundation
# Author: Richard Purdie <richard.purdie@linuxfoundation.org>
# Author: Quentin Schulz <foss@0leil.net>
#
# SPDX-License-Identifier: MIT
#


import json
import subprocess
import collections
import sys
import os
import itertools

from urllib.request import urlopen, URLError

# NOTE: these variables contain default values in case we are not able to fetch
# the releases.json file from https://dashboard.yoctoproject.org/releases.json
activereleases = ["whinlatter", "scarthgap", "kirkstone"]
devbranch = "wrynose"
ltsseries = ["wrynose", "scarthgap", "kirkstone"]
release_series = collections.OrderedDict({
    "wrynose": "6.0",
    "whinlatter": "5.3",
    "scarthgap": "5.0",
    "kirkstone": "4.0",
})
bitbake_mapping = collections.OrderedDict({
    "wrynose": "2.18",
    "whinlatter": "2.16",
    "scarthgap": "2.8",
    "kirkstone": "2.0",
})

releases_from_json = {}

# Use the local releases.json file if found, fetch it from the dashboard otherwise
releases_json_path = os.path.join(os.path.dirname(os.path.realpath(__file__)), "releases.json")
try:
    with open(releases_json_path, "r") as f:
        releases_from_json = json.load(f)
except FileNotFoundError:
    print("Fetching releases.json from https://dashboard.yoctoproject.org/releases.json...",
          file=sys.stderr)
    try:
        with urlopen("https://dashboard.yoctoproject.org/releases.json") as r, \
                open(releases_json_path, "w") as f:
            releases_from_json = json.load(r)
            json.dump(releases_from_json, f)
    except URLError:
        print("WARNING: tried to fetch https://dashboard.yoctoproject.org/releases.json "
              "but failed, using default values for active releases", file=sys.stderr)
        pass

if releases_from_json:
    release_series = collections.OrderedDict()
    activereleases = []
    devbranch = ""
    ltsseries = []
    bitbake_mapping = collections.OrderedDict()

    for release in releases_from_json:
        codename = release["release_codename"].lower()
        release_series[codename] = release["series_version"]
        if release["status"] == "Active Development":
            devbranch = codename
        if release["series"] == "current":
            activereleases.append(codename)
        if "LTS until" in release["status"]:
            ltsseries.append(codename)
        if release["bitbake_version"]:
            bitbake_mapping[codename] = release["bitbake_version"]

    activereleases.remove(devbranch)

# used by run-docs-builds to get the default page
if len(sys.argv) > 1 and sys.argv[1] == "getlatest":
    print(activereleases[0])
    sys.exit(0)

print(f"activereleases calculated to be {activereleases}")
print(f"devbranch calculated to be {devbranch}")
print(f"ltsseries calculated to be {ltsseries}")

ourversion = None
ourseries = None
ourbranch = None
bitbakeversion = None
docconfver = None
head_commit = None
is_branch_tip = False

# Test that we are building from a Git repository
try:
    subprocess.run(["git", "rev-parse", "--is-inside-work-tree"],
                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
except subprocess.CalledProcessError:
    sys.exit("Building yocto-docs must be done from its Git repository.\n"
             "Clone the documentation repository with the following command:\n"
             "git clone https://git.yoctoproject.org/yocto-docs ")

# Test tags exist and inform the user to fetch if not
try:
    subprocess.run(["git", "show", "yocto-%s" % release_series[activereleases[0]]],
                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
except subprocess.CalledProcessError:
    sys.exit("Please run 'git fetch --tags' before building the documentation")

# Try and figure out what we are
tags = subprocess.run(["git", "tag", "--points-at", "HEAD"],
                      stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True).stdout
for t in tags.split():
    if t.startswith("yocto-"):
        ourversion = t[6:]
        seriesversion = ".".join(ourversion.split(".")[0:2])
        for series in release_series:
            if release_series[series] == seriesversion:
                ourseries = series
                bitbakeversion = bitbake_mapping[ourseries]
                break
        break

if ourversion is None:
    # We're floating on a branch
    branch = subprocess.run(["git", "branch", "--show-current"],
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                            universal_newlines=True).stdout.strip()

    if branch == "" or branch not in list(release_series.keys()) + ["master", "master-next"]:
        # We're not on a known release branch so we have to guess. Compare the
        # numbers of commits from each release branch and assume the smallest
        # number of commits is the one we're based off
        possible_branch = None
        branch_count = 0
        for b in itertools.chain(release_series.keys(), ["master"]):
            result = subprocess.run(["git", "log", "--format=oneline", "HEAD..origin/" + b],
                                    stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                    universal_newlines=True)
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
        ourversion = "dev"
        ourseries = devbranch
        bitbakeversion = ourversion
    elif branch == "master-next":
        ourversion = "next"
        ourseries = devbranch
        bitbakeversion = ourversion
    else:
        ourseries = branch
        bitbakeversion = bitbake_mapping[ourseries]
        ourversion = release_series[branch]
        head_commit = subprocess.run(["git", "rev-parse", "--short", "HEAD"],
                                     stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                     universal_newlines=True).stdout.strip()
        branch_commit = subprocess.run(["git", "rev-parse", "--short", branch],
                                       stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                       universal_newlines=True).stdout.strip()
        if head_commit != branch_commit:
            ourversion += f"-{head_commit}"
        else:
            is_branch_tip = True
            ourversion += "-tip"

series = [k for k in release_series]
previousseries = series[series.index(ourseries) + 1:] or [""]
lastlts = [k for k in previousseries if k in ltsseries] or "dunfell"

latestreltag = subprocess.run(["git", "describe", "--abbrev=0", "--tags", "--match", "yocto-*"],
                              stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True).stdout
latestreltag = latestreltag.strip()
if latestreltag:
    if latestreltag.startswith("yocto-"):
        latesttag = latestreltag[6:]
else:
    # fallback on the calculated version
    print("Did not find a tag with 'git describe', falling back to %s" % ourversion)
    latestreltag = "yocto-" + ourversion
    latesttag = ourversion

print("Version calculated to be %s" % ourversion)
print("Latest release tag found is %s" % latestreltag)
print("Release series calculated to be %s" % ourseries)
print("Bitbake version calculated to be %s" % bitbakeversion)

# The &DISTRO; replacement will be mostly right when just equaling ourversion.
# When we're on a random commit, in that case the DISTRO value will
# contain the hash. This would for example be "5.3-4d2acc043".
# This will not happen when publishing the documentation from the
# autobuilder as we don't build specific commits - this should only happen
# for development.
distro = ourversion
# Few exceptions though:
if is_branch_tip:
    # we're on a branch tip, in that case the closest match is the latest tag
    # from that branch. Some instructions rely on DISTRO to provide tag names,
    # etc. so it makes sense provide the latest tag from that branch.
    distro = latesttag
elif distro in ["dev", "next"]:
    # When building on master or master-next, make the distro the version of the
    # devbranch.
    distro = release_series[devbranch]

print(f"DISTRO calculated to be {distro}")

replacements = {
    "DISTRO": distro,
    "DISTRO_LATEST_TAG": latesttag,
    "DISTRO_RELEASE_SERIES": release_series[ourseries],
    "DISTRO_NAME_NO_CAP": ourseries,
    "DISTRO_NAME": ourseries.capitalize(),
    "DISTRO_NAME_NO_CAP_MINUS_ONE": previousseries[0],
    "DISTRO_NAME_NO_CAP_LTS": lastlts[0],
    "YOCTO_DOC_VERSION": ourversion,
    "DOCCONF_VERSION": ourversion,
    "BITBAKE_SERIES": bitbakeversion,
}

# 3.4 onwards doesn't have poky version
# Early 3.4 release docs do reference it though
poky_mapping = {
    "3.4": "26.0",
    "3.3": "25.0",
    "3.2": "24.0",
    "3.1": "23.0",
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

def get_latest_tag(branch: str) -> str:
    """
    Get the latest tag of `branch`.
    """
    branch_versions = subprocess.run(["git", "tag", "--list", f'yocto-{release_series[branch]}*'],
                                     stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                     universal_newlines=True).stdout.split()
    branch_versions = sorted(
        [v.replace("yocto-" + release_series[branch] + ".", "")
         .replace("yocto-" + release_series[branch], "0") for v in branch_versions],
        key=int)
    if not branch_versions:
        return ""

    version = release_series[branch]
    if branch_versions[-1] != "0":
        version = version + "." + branch_versions[-1]
    return version

def get_abbrev_hash(ref: str) -> str:
    """
    Get the abbreviated hash of a ref using a call to git rev-parse.
    """
    return subprocess.run(["git", "rev-parse", "--short", ref],
                          stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                          universal_newlines=True).stdout.strip()

versions = []
with open("sphinx-static/switchers.js.in", "r") as r, open("sphinx-static/switchers.js", "w") as w:
    lines = r.readlines()
    for line in lines:
        if "VERSIONS_PLACEHOLDER" in line:
            w.write(f"    'dev': {{'title': 'Unstable (dev)', 'hash': '{get_abbrev_hash('master')}'}},\n")
            for branch in activereleases:
                series = release_series[branch]
                w.write(f"    '{series}-tip': {{'title': '{branch.capitalize()} ({get_latest_tag(branch)})', "
                        f"'hash': '{get_abbrev_hash(branch)}'}},\n")
        elif "ALL_RELEASES_PLACEHOLDER" in line:
            for series in release_series:
                w.write(f"    '{release_series[series]}': "
                        f"{{'codename': '{series.capitalize()}', "
                        f"'latest_tag': '{get_latest_tag(series)}'}},\n")
        elif "LATEST_VERSION_PLACEHOLDER" in line:
            latest_series = release_series[activereleases[0]]
            w.write(f"    '{latest_series}-tip'\n")
        else:
            w.write(line)

print("switchers.js generated from switchers.js.in")

# generate releases.rst

yocto_tags = subprocess.run(["git", "tag", "--list", "--sort=version:refname", "yocto-*"],
                            stdout=subprocess.PIPE, stderr=subprocess.PIPE, universal_newlines=True).stdout.split()
tags = [tag[6:] for tag in yocto_tags]

with open('releases.rst', 'w') as f:
    f.write('===========================\n')
    f.write(' Supported Release Manuals\n')
    f.write('===========================\n')
    f.write('\n')

    for activerelease in activereleases:
        title = "Release Series %s (%s)" % (release_series[activerelease], activerelease)
        f.write('*' * len(title) + '\n')
        f.write(title + '\n')
        f.write('*' * len(title) + '\n')
        f.write('\n')

        for tag in tags:
            if tag == release_series[activerelease] or tag.startswith('%s.' % release_series[activerelease]):
                f.write('- :yocto_docs:`%s Documentation </%s>`\n' % (tag, tag))
        f.write('\n')

    f.write('==========================\n')
    f.write(' Outdated Release Manuals\n')
    f.write('==========================\n')
    f.write('\n')

    for series in release_series:
        if series == devbranch or series in activereleases:
            continue

        if series == "jethro-pre":
            continue

        title = "Release Series %s (%s)" % (release_series[series], series)
        f.write('*' * len(title) + '\n')
        f.write(title + '\n')
        f.write('*' * len(title) + '\n')
        f.write('\n')
        if series == "jethro":
            f.write('- :yocto_docs:`1.9 Documentation </1.9>`\n')
        for tag in tags:
            if tag == release_series[series] or tag.startswith('%s.' % release_series[series]):
                f.write('- :yocto_docs:`%s Documentation </%s>`\n' % (tag, tag))
        f.write('\n')
