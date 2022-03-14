#!/usr/bin/env python3
#
# Conversion script to update SRC_URI to add branch to git urls
#
# Copyright (C) 2021 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

import re
import os
import sys
import tempfile
import shutil
import mimetypes

if len(sys.argv) < 2:
    print("Please specify a directory to run the conversion script against.")
    sys.exit(1)

def processfile(fn):
    def matchline(line):
        if "MIRROR" in line or ".*" in line or "GNOME_GIT" in line:
            return False
        return True
    print("processing file '%s'" % fn)
    try:
        if "distro_alias.inc" in fn or "linux-yocto-custom.bb" in fn:
            return
        fh, abs_path = tempfile.mkstemp()
        modified = False
        with os.fdopen(fh, 'w') as new_file:
            with open(fn, "r") as old_file:
                for line in old_file:
                    if ("git://" in line or "gitsm://" in line) and "branch=" not in line and matchline(line):
                        if line.endswith('"\n'):
                            line = line.replace('"\n', ';branch=master"\n')
                        elif re.search('\s*\\\\$', line):
                            line = re.sub('\s*\\\\$', ';branch=master \\\\', line)
                        modified = True
                    if ("git://" in line or "gitsm://" in line) and "github.com" in line and "protocol=https" not in line and matchline(line):
                        if "protocol=git" in line:
                            line = line.replace('protocol=git', 'protocol=https')
                        elif line.endswith('"\n'):
                            line = line.replace('"\n', ';protocol=https"\n')
                        elif re.search('\s*\\\\$', line):
                            line = re.sub('\s*\\\\$', ';protocol=https \\\\', line)
                        modified = True
                    new_file.write(line)
        if modified:
            shutil.copymode(fn, abs_path)
            os.remove(fn)
            shutil.move(abs_path, fn)
    except UnicodeDecodeError:
        pass

ourname = os.path.basename(sys.argv[0])
ourversion = "0.1"

if os.path.isfile(sys.argv[1]):
    processfile(sys.argv[1])
    sys.exit(0)

for targetdir in sys.argv[1:]:
    print("processing directory '%s'" % targetdir)
    for root, dirs, files in os.walk(targetdir):
        for name in files:
            if name == ourname:
                continue
            fn = os.path.join(root, name)
            if os.path.islink(fn):
                continue
            if "/.git/" in fn or fn.endswith(".html") or fn.endswith(".patch") or fn.endswith(".m4") or fn.endswith(".diff"):
                continue
            processfile(fn)

print("All files processed with version %s" % ourversion)
