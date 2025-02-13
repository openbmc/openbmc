#!/usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
# This script is to be called by b4:
# - through b4.send-auto-cc-cmd with "send-auto-cc-cmd" as first argument,
#
# When send-auto-cc-cmd is passed:
#
#  This returns the list of Cc recipients for a patch.
#
# This script takes as stdin a patch.

import subprocess
import sys

cmd = sys.argv[1]
if cmd != "send-auto-cc-cmd":
    sys.exit(-1)

patch = sys.stdin.read()

if subprocess.call(["which", "lsdiff"], stdout=subprocess.DEVNULL) != 0:
    print("lsdiff missing from host, please install patchutils")
    sys.exit(-1)

files = subprocess.check_output(["lsdiff", "--strip-match=1", "--strip=1", "--include=doc/*"],
                                input=patch, text=True)
if len(files):
    print("docs@lists.yoctoproject.org")
else:
# Handle patches made with --no-prefix
    files = subprocess.check_output(["lsdiff", "--include=doc/*"],
                                    input=patch, text=True)
    if len(files):
        print("docs@lists.yoctoproject.org")

sys.exit(0)
