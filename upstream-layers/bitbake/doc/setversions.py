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
import json
import os
import re
import subprocess
import sys
import textwrap

from urllib.request import urlopen, URLError

# NOTE: the following variables contain default values in case we are not able to fetch
# the releases.json file from https://dashboard.yoctoproject.org/releases.json
DEVBRANCH = "2.18"
LTSSERIES = ["2.8", "2.0"]
ACTIVERELEASES = ["2.16"] + LTSSERIES
YOCTO_MAPPING = {
    "2.18": "wrynose",
    "2.16": "whinlatter",
    "2.8": "scarthgap",
    "2.0": "kirkstone",
}

RELEASES_FROM_JSON = {}

# Use the local releases.json file if found, fetch it from the dashboard otherwise
releases_json_path = os.path.join(os.path.dirname(os.path.realpath(__file__)), "releases.json")
try:
    with open(releases_json_path, "r") as f:
        RELEASES_FROM_JSON = json.load(f)
except FileNotFoundError:
    print("Fetching releases.json from https://dashboard.yoctoproject.org/releases.json...",
          file=sys.stderr)
    try:
        with urlopen("https://dashboard.yoctoproject.org/releases.json") as r, \
                open(releases_json_path, "w") as f:
            RELEASES_FROM_JSON = json.load(r)
            json.dump(RELEASES_FROM_JSON, f)
    except URLError:
        print("WARNING: tried to fetch https://dashboard.yoctoproject.org/releases.json "
              "but failed, using default values for active releases", file=sys.stderr)
        pass

if RELEASES_FROM_JSON:
    ACTIVERELEASES = []
    DEVBRANCH = ""
    LTSSERIES = []
    YOCTO_MAPPING = {}

    for release in RELEASES_FROM_JSON:
        bb_ver = release["bitbake_version"]
        if release["status"] == "Active Development":
            DEVBRANCH = bb_ver
        if release["series"] == "current":
            ACTIVERELEASES.append(bb_ver)
        if "LTS until" in release["status"]:
            LTSSERIES.append(bb_ver)
        if release["bitbake_version"]:
            YOCTO_MAPPING[bb_ver] = release["release_codename"]

    ACTIVERELEASES.remove(DEVBRANCH)

print(f"ACTIVERELEASES calculated to be {ACTIVERELEASES}", file=sys.stderr)
print(f"DEVBRANCH calculated to be {DEVBRANCH}", file=sys.stderr)
print(f"LTSSERIES calculated to be {LTSSERIES}", file=sys.stderr)

BB_RELEASE_TAG_RE = re.compile(r"^[0-9]+\.[0-9]+\.[0-9]+$")

def get_current_version():
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
            return t

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
        return "dev"

    if branch == "master-next":
        return "next"

    ourversion = branch
    head_commit = subprocess.run(["git", "rev-parse", "--short", "HEAD"],
                                 stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                 universal_newlines=True).stdout.strip()
    branch_commit = subprocess.run(["git", "rev-parse", "--short", branch],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                                   universal_newlines=True).stdout.strip()
    if head_commit != branch_commit:
        ourversion += f" ({head_commit})"

    return ourversion

def write_switchers_js(js_in, js_out, current_version):
    with open(js_in, "r") as r, open(js_out, "w") as w:
        lines = r.readlines()
        for line in lines:
            if "VERSIONS_PLACEHOLDER" in line:
                if current_version != "dev":
                    w.write("    'dev': 'Unstable (dev)',\n")
                for series in ACTIVERELEASES:
                    w.write(f"    '{series}': '{series} ({YOCTO_MAPPING[series]})',\n")
            else:
                w.write(line)
        print("switchers.js generated from switchers.js.in")

def _release_section(series_version: str, codename: str, bitbake_version: str) -> str:
    """
    Helper function to generate a release section, as:

    ********************
    Release Series xxxxx
    ********************

    - <link to manual>
    """
    section_length = len(series_version) + len(codename) + 18
    return textwrap.dedent(
        f"""\
        {'*' * section_length}
        Release Series {series_version} ({codename})
        {'*' * section_length}

        - :yocto_docs:`BitBake {bitbake_version} User Manual </bitbake/{bitbake_version}/>`

        """)

def write_releases_rst(releases_rst_out: str):
    """
    Generates the releases.rst file automatically, based on what is found
    in the releases.json file.
    """
    with open(releases_rst_out, "w") as f:
        f.write(textwrap.dedent("""\
            .. SPDX-License-Identifier: CC-BY-2.5

            =================================
            BitBake Supported Release Manuals
            =================================

            """))

        for release in RELEASES_FROM_JSON:
            if release["status"] == "Active Development":
                continue

            if not release["bitbake_version"]:
                continue

            if release["series"] == "current":
                f.write(_release_section(
                    release["series_version"],
                    release["release_codename"],
                    release["bitbake_version"]))

        f.write(textwrap.dedent("""\
            ================================
            BitBake Outdated Release Manuals
            ================================

            """))

        for release in RELEASES_FROM_JSON:
            if not release["series"] == "previous":
                continue

            if not release["bitbake_version"]:
                continue

            f.write(_release_section(
                release["series_version"],
                release["release_codename"],
                release["bitbake_version"]))

        # old legacy links, which cannot be auto-generated
        f.write(textwrap.dedent(
            """\
            - :yocto_docs:`3.1.2 BitBake User Manual </3.1.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.1 BitBake User Manual </3.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.1.1 BitBake User Manual </3.1.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.1.3 BitBake User Manual </3.1.3/bitbake-user-manual/bitbake-user-manual.html>`

            *************************
            Release Series 3.0 (Zeus)
            *************************

            - :yocto_docs:`3.0 BitBake User Manual </3.0/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.0.1 BitBake User Manual </3.0.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.0.2 BitBake User Manual </3.0.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.0.3 BitBake User Manual </3.0.3/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`3.0.4 BitBake User Manual </3.0.4/bitbake-user-manual/bitbake-user-manual.html>`

            ****************************
            Release Series 2.7 (Warrior)
            ****************************

            - :yocto_docs:`2.7 BitBake User Manual </2.7/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.7.1 BitBake User Manual </2.7.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.7.2 BitBake User Manual </2.7.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.7.3 BitBake User Manual </2.7.3/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.7.4 BitBake User Manual </2.7.4/bitbake-user-manual/bitbake-user-manual.html>`

            *************************
            Release Series 2.6 (Thud)
            *************************

            - :yocto_docs:`2.6 BitBake User Manual </2.6/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.6.1 BitBake User Manual </2.6.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.6.2 BitBake User Manual </2.6.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.6.3 BitBake User Manual </2.6.3/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.6.4 BitBake User Manual </2.6.4/bitbake-user-manual/bitbake-user-manual.html>`

            *************************
            Release Series 2.5 (Sumo)
            *************************

            - :yocto_docs:`2.5 Documentation </2.5>`
            - :yocto_docs:`2.5.1 Documentation </2.5.1>`
            - :yocto_docs:`2.5.2 Documentation </2.5.2>`
            - :yocto_docs:`2.5.3 Documentation </2.5.3>`

            **************************
            Release Series 2.4 (Rocko)
            **************************

            - :yocto_docs:`2.4 BitBake User Manual </2.4/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.4.1 BitBake User Manual </2.4.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.4.2 BitBake User Manual </2.4.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.4.3 BitBake User Manual </2.4.3/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.4.4 BitBake User Manual </2.4.4/bitbake-user-manual/bitbake-user-manual.html>`

            *************************
            Release Series 2.3 (Pyro)
            *************************

            - :yocto_docs:`2.3 BitBake User Manual </2.3/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.3.1 BitBake User Manual </2.3.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.3.2 BitBake User Manual </2.3.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.3.3 BitBake User Manual </2.3.3/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.3.4 BitBake User Manual </2.3.4/bitbake-user-manual/bitbake-user-manual.html>`

            **************************
            Release Series 2.2 (Morty)
            **************************

            - :yocto_docs:`2.2 BitBake User Manual </2.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.2.1 BitBake User Manual </2.2.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.2.2 BitBake User Manual </2.2.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.2.3 BitBake User Manual </2.2.3/bitbake-user-manual/bitbake-user-manual.html>`

            ****************************
            Release Series 2.1 (Krogoth)
            ****************************

            - :yocto_docs:`2.1 BitBake User Manual </2.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.1.1 BitBake User Manual </2.1.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.1.2 BitBake User Manual </2.1.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.1.3 BitBake User Manual </2.1.3/bitbake-user-manual/bitbake-user-manual.html>`

            ***************************
            Release Series 2.0 (Jethro)
            ***************************

            - :yocto_docs:`1.9 BitBake User Manual </1.9/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.0 BitBake User Manual </2.0/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.0.1 BitBake User Manual </2.0.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.0.2 BitBake User Manual </2.0.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`2.0.3 BitBake User Manual </2.0.3/bitbake-user-manual/bitbake-user-manual.html>`

            *************************
            Release Series 1.8 (Fido)
            *************************

            - :yocto_docs:`1.8 BitBake User Manual </1.8/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.8.1 BitBake User Manual </1.8.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.8.2 BitBake User Manual </1.8.2/bitbake-user-manual/bitbake-user-manual.html>`

            **************************
            Release Series 1.7 (Dizzy)
            **************************

            - :yocto_docs:`1.7 BitBake User Manual </1.7/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.7.1 BitBake User Manual </1.7.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.7.2 BitBake User Manual </1.7.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.7.3 BitBake User Manual </1.7.3/bitbake-user-manual/bitbake-user-manual.html>`

            **************************
            Release Series 1.6 (Daisy)
            **************************

            - :yocto_docs:`1.6 BitBake User Manual </1.6/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.6.1 BitBake User Manual </1.6.1/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.6.2 BitBake User Manual </1.6.2/bitbake-user-manual/bitbake-user-manual.html>`
            - :yocto_docs:`1.6.3 BitBake User Manual </1.6.3/bitbake-user-manual/bitbake-user-manual.html>`
            """))
