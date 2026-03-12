#!/usr/bin/env python3
#
# This file defines is used in doc/conf.py to setup the version information for
# the documentation:
# - get_current_version() used in doc/conf.py computes the current version by
#   trying to guess the approximate versions we're at using git tags and
#   branches from the repository.
# - write_switchers_js() write the switchers.js file used for switching between
#   versions of the documentation.
#
# Copyright (c) 2026 Antonin Godard <antonin.godard@bootlin.com>
#
# SPDX-License-Identifier: MIT
#

import itertools
import re
import subprocess
import sys

DEVBRANCH = "2.18"
LTSSERIES = ["2.8", "2.0"]
ACTIVERELEASES = ["2.16"] + LTSSERIES

YOCTO_MAPPING = {
    "2.18": "wrynose",
    "2.16": "whinlatter",
    "2.12": "walnascar",
    "2.10": "styhead",
    "2.8": "scarthgap",
    "2.6": "nanbield",
    "2.4": "mickledore",
    "2.2": "langdale",
    "2.0": "kirkstone",
    "1.52": "honister",
    "1.50": "hardknott",
    "1.48": "gatesgarth",
    "1.46": "dunfell",
}

BB_RELEASE_TAG_RE = re.compile(r"^[0-9]+\.[0-9]+\.[0-9]+$")

def get_current_version():
    ourversion = None

    # Test that we are building from a Git repository
    try:
        subprocess.run(["git", "rev-parse", "--is-inside-work-tree"],
                       stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
    except subprocess.CalledProcessError:
        sys.exit("Building bitbake's documentation must be done from its Git repository.\n"
                 "Clone the repository with the following command:\n"
                 "git clone https://git.openembedded.org/bitbake ")

    # Test tags exist and inform the user to fetch if not
    try:
        subprocess.run(["git", "show", f"{LTSSERIES[0]}.0"],
                       stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
    except subprocess.CalledProcessError:
        sys.exit("Please run 'git fetch --tags' before building the documentation")


    # Try and figure out what we are
    tags = subprocess.run(["git", "tag", "--points-at", "HEAD"],
                          stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                          universal_newlines=True).stdout
    for t in tags.split():
        if re.match(BB_RELEASE_TAG_RE, t):
            ourversion = t
            break

    if ourversion:
        # We're a tagged release
        components = ourversion.split(".")
    else:
        # We're floating on a branch
        branch = subprocess.run(["git", "branch", "--show-current"],
                                stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                universal_newlines=True).stdout.strip()

        if branch == "" or branch not in list(YOCTO_MAPPING.keys()) + ["master", "master-next"]:
            # We're not on a known release branch so we have to guess. Compare the
            # numbers of commits from each release branch and assume the smallest
            # number of commits is the one we're based off
            possible_branch = None
            branch_count = 0
            for b in itertools.chain(YOCTO_MAPPING.keys(), ["master"]):
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
        elif branch == "master-next":
            ourversion = "next"
        else:
            ourversion = branch
            head_commit = subprocess.run(["git", "rev-parse", "--short", "HEAD"],
                                         stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                         universal_newlines=True).stdout.strip()
            branch_commit = subprocess.run(["git", "rev-parse", "--short", branch],
                                            stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                            universal_newlines=True).stdout.strip()
            if head_commit != branch_commit:
                ourversion += f" ({head_commit})"

    print("Version calculated to be %s" % ourversion)
    return ourversion

def write_switchers_js(js_in, js_out, current_version):
    with open(js_in, "r") as r, open(js_out, "w") as w:
        lines = r.readlines()
        for line in lines:
            if "VERSIONS_PLACEHOLDER" in line:
                if current_version != "dev":
                    w.write(f"    'dev': 'Unstable (dev)',\n")
                for series in ACTIVERELEASES:
                    w.write(f"    '{series}': '{series} ({YOCTO_MAPPING[series]})',\n")
            else:
                w.write(line)
        print("switchers.js generated from switchers.js.in")
