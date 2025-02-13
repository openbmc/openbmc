#!/usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
# This script is to be called by b4:
# - through the b4.prep-perpatch-check-cmd with "prep-perpatch-check-cmd" as
#   first argument,
# - through b4.send-auto-cc-cmd with "send-auto-cc-cmd" as first argument,
# - through b4.send-auto-to-cmd with "send-auto-to-cmd" as first argument,
#
# When prep-perpatch-check-cmd is passsed:
#
#  This checks that a patch makes changes to at most one project in the poky
#  combo repo (that is, out of yocto-docs, bitbake, openembedded-core combined
#  into poky and the poky-specific files).
#
#  Printing something to stdout in this file will result in b4 prep --check fail
#  for the currently parsed patch.
#
#  It checks that all patches in the series make changes to at most one project.
#
# When send-auto-cc-cmd is passed:
#
#  This returns the list of Cc recipients for a patch.
#
# When send-auto-to-cmd is passed:
#
#  This returns the list of To recipients for a patch.
#
# This script takes as stdin a patch.

import pathlib
import re
import shutil
import subprocess
import sys

cmd = sys.argv[1]

patch = sys.stdin.readlines()

# Subject field is used to identify the last patch as this script is called for
# each patch. We edit the same file in a series by using the References field
# unique identifier to check which projects are modified by earlier patches in
# the series. To avoid cluttering the disk, the last patch in the list removes
# that shared file.
re_subject = re.compile(r'^Subject:.*\[.*PATCH.*\s(\d+)/\1')
re_ref = re.compile(r'^References: <(.*)>$')

subject = None
ref = None

if not shutil.which("lsdiff"):
    print("lsdiff missing from host, please install patchutils")
    sys.exit(-1)

try:
    one_patch_series = False
    for line in patch:
        subject = re_subject.match(line)
        if subject:
            # Handle [PATCH 1/1]
            if subject.group(1) == 1:
                one_patch_series = True
            break
        if re.match(r'^Subject: .*\[.*PATCH[^/]*\]', line):
            # Single patch is named [PATCH] but if there are prefix, it could be
            # [PATCH prefix], so handle everything that doesn't have a /
            # character which is used as separator between current patch number
            # and total patch number
            one_patch_series = True
            break

    if cmd == "prep-perpatch-check-cmd" and not one_patch_series:
        for line in patch:
            ref = re_ref.match(line)
            if ref:
                break

        if not ref:
            print("Failed to find ref to cover letter (References:)...")
            sys.exit(-2)

        ref = ref.group(1)
        series_check = pathlib.Path(f".tmp-{ref}")

    patch = "".join(patch)

    if cmd == "send-auto-cc-cmd":
        # Patches to BitBake documentation should also go to yocto-docs mailing list
        project_paths = {
                "yocto-docs": ["bitbake/doc/*"],
        }
    else:
        project_paths = {
                "bitbake": ["bitbake/*"],
                "yocto-docs": ["documentation/*"],
                "poky": [
                    "meta-poky/*",
                    "meta-yocto-bsp/*",
                    "README.hardware.md",
                    "README.poky.md",
                    # scripts/b4-wrapper-poky.py is only run by b4 when in poky
                    # git repo. With that limitation, changes made to .b4-config
                    # can only be for poky's and not OE-Core's as only poky's is
                    # stored in poky git repo.
                    ".b4-config",
                    ],
        }

    # List of projects touched by this patch
    projs = []

    # Any file not matched by any path in project_paths means it is from
    # OE-Core.
    # When matching some path in project_paths, remove the matched files from
    # that list.
    files_left = subprocess.check_output(["lsdiff", "--strip-match=1", "--strip=1"],
                                         input=patch, text=True)
    files_left = set(files_left)

    for proj, proj_paths in project_paths.items():
        lsdiff_args = [f"--include={path}" for path in proj_paths]
        files = subprocess.check_output(["lsdiff", "--strip-match=1", "--strip=1"] + lsdiff_args,
                                        input=patch, text=True)
        if len(files):
            files_left = files_left - set(files)
            projs.append(proj)
            continue

        # Handle patches made with --no-prefix
        files = subprocess.check_output(["lsdiff"] + lsdiff_args,
                                        input=patch, text=True)
        if len(files):
            files_left = files_left - set(files)
            projs.append(proj)

    # Catch-all for everything not poky-specific or in bitbake/yocto-docs
    if len(files_left) and cmd != "send-auto-cc-cmd":
        projs.append("openembedded-core")

    if cmd == "prep-perpatch-check-cmd":
        if len(projs) > 1:
            print(f"Diff spans more than one project ({', '.join(sorted(projs))}), split into multiple commits...")
            sys.exit(-3)

        # No need to check other patches in the series as there aren't any
        if one_patch_series:
            sys.exit(0)

        # This should be replaced once b4 supports prep-perseries-check-cmd (or something similar)

        if series_check.exists():
            # NOT race-free if b4 decides to parallelize prep-perpatch-check-cmd
            series_projs = series_check.read_text().split('\n')
        else:
            series_projs = []

        series_projs += projs
        uniq_series_projs = set(series_projs)
        # NOT race-free, if b4 decides to parallelize prep-perpatch-check-cmd
        series_check.write_text('\n'.join(uniq_series_projs))

        if len(uniq_series_projs) > 1:
            print(f"Series spans more than one project ({', '.join(sorted(uniq_series_projs))}), split into multiple series...")
            sys.exit(-4)
    else:  # send-auto-cc-cmd / send-auto-to-cmd
        ml_projs = {
            "bitbake": "bitbake-devel@lists.openembedded.org",
            "yocto-docs": "docs@lists.yoctoproject.org",
            "poky": "poky@lists.yoctoproject.org",
            "openembedded-core": "openembedded-core@lists.openembedded.org",
        }

        print("\n".join([ml_projs[ml] for ml in projs]))

    sys.exit(0)
finally:
    # Last patch in the series, cleanup tmp file
    if subject and ref and series_check.exists():
        series_check.unlink()
