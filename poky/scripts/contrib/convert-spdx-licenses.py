#!/usr/bin/env python3
#
# Conversion script to change LICENSE entries to SPDX identifiers
#
# Copyright (C) 2021-2022 Richard Purdie
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

license_map = {
"AGPL-3" : "AGPL-3.0-only",
"AGPL-3+" : "AGPL-3.0-or-later",
"AGPLv3" : "AGPL-3.0-only",
"AGPLv3+" : "AGPL-3.0-or-later",
"AGPLv3.0" : "AGPL-3.0-only",
"AGPLv3.0+" : "AGPL-3.0-or-later",
"AGPL-3.0" : "AGPL-3.0-only",
"AGPL-3.0+" : "AGPL-3.0-or-later",
"BSD-0-Clause" : "0BSD",
"GPL-1" : "GPL-1.0-only",
"GPL-1+" : "GPL-1.0-or-later",
"GPLv1" : "GPL-1.0-only",
"GPLv1+" : "GPL-1.0-or-later",
"GPLv1.0" : "GPL-1.0-only",
"GPLv1.0+" : "GPL-1.0-or-later",
"GPL-1.0" : "GPL-1.0-only",
"GPL-1.0+" : "GPL-1.0-or-later",
"GPL-2" : "GPL-2.0-only",
"GPL-2+" : "GPL-2.0-or-later",
"GPLv2" : "GPL-2.0-only",
"GPLv2+" : "GPL-2.0-or-later",
"GPLv2.0" : "GPL-2.0-only",
"GPLv2.0+" : "GPL-2.0-or-later",
"GPL-2.0" : "GPL-2.0-only",
"GPL-2.0+" : "GPL-2.0-or-later",
"GPL-3" : "GPL-3.0-only",
"GPL-3+" : "GPL-3.0-or-later",
"GPLv3" : "GPL-3.0-only",
"GPLv3+" : "GPL-3.0-or-later",
"GPLv3.0" : "GPL-3.0-only",
"GPLv3.0+" : "GPL-3.0-or-later",
"GPL-3.0" : "GPL-3.0-only",
"GPL-3.0+" : "GPL-3.0-or-later",
"LGPLv2" : "LGPL-2.0-only",
"LGPLv2+" : "LGPL-2.0-or-later",
"LGPLv2.0" : "LGPL-2.0-only",
"LGPLv2.0+" : "LGPL-2.0-or-later",
"LGPL-2.0" : "LGPL-2.0-only",
"LGPL-2.0+" : "LGPL-2.0-or-later",
"LGPL2.1" : "LGPL-2.1-only",
"LGPL2.1+" : "LGPL-2.1-or-later",
"LGPLv2.1" : "LGPL-2.1-only",
"LGPLv2.1+" : "LGPL-2.1-or-later",
"LGPL-2.1" : "LGPL-2.1-only",
"LGPL-2.1+" : "LGPL-2.1-or-later",
"LGPLv3" : "LGPL-3.0-only",
"LGPLv3+" : "LGPL-3.0-or-later",
"LGPL-3.0" : "LGPL-3.0-only",
"LGPL-3.0+" : "LGPL-3.0-or-later",
"MPL-1" : "MPL-1.0",
"MPLv1" : "MPL-1.0",
"MPLv1.1" : "MPL-1.1",
"MPLv2" : "MPL-2.0",
"MIT-X" : "MIT",
"MIT-style" : "MIT",
"openssl" : "OpenSSL",
"PSF" : "PSF-2.0",
"PSFv2" : "PSF-2.0",
"Python-2" : "Python-2.0",
"Apachev2" : "Apache-2.0",
"Apache-2" : "Apache-2.0",
"Artisticv1" : "Artistic-1.0",
"Artistic-1" : "Artistic-1.0",
"AFL-2" : "AFL-2.0",
"AFL-1" : "AFL-1.2",
"AFLv2" : "AFL-2.0",
"AFLv1" : "AFL-1.2",
"CDDLv1" : "CDDL-1.0",
"CDDL-1" : "CDDL-1.0",
"EPLv1.0" : "EPL-1.0",
"FreeType" : "FTL",
"Nauman" : "Naumen",
"tcl" : "TCL",
"vim" : "Vim",
"SGIv1" : "SGI-1",
}

def processfile(fn):
    print("processing file '%s'" % fn)
    try:
        fh, abs_path = tempfile.mkstemp()
        modified = False
        with os.fdopen(fh, 'w') as new_file:
            with open(fn, "r") as old_file:
                for line in old_file:
                    if not line.startswith("LICENSE"):
                        new_file.write(line)
                        continue
                    orig = line
                    for license in sorted(license_map, key=len, reverse=True):
                        for ending in ['"', "'", " ", ")"]:
                            line = line.replace(license + ending, license_map[license] + ending)
                    if orig != line:
                        modified = True
                    new_file.write(line)
        new_file.close()
        if modified:
            shutil.copymode(fn, abs_path)
            os.remove(fn)
            shutil.move(abs_path, fn)
    except UnicodeDecodeError:
        pass

ourname = os.path.basename(sys.argv[0])
ourversion = "0.01"

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
            if "/.git/" in fn or fn.endswith(".html") or fn.endswith(".patch") or fn.endswith(".m4") or fn.endswith(".diff") or fn.endswith(".orig"):
                continue
            processfile(fn)

print("All files processed with version %s" % ourversion)
