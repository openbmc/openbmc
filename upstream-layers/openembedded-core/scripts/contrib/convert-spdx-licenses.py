#!/usr/bin/env python3
#
# Conversion script to change LICENSE entries to SPDX identifiers
#
# Copyright (C) 2021-2022 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#


from abc import abstractmethod, ABC
from enum import Enum
from pathlib import Path
import json
import mimetypes
import os
import re
import shutil
import sys
import tempfile

THIS_DIR = Path(__file__).parent

sys.path.insert(0, str(THIS_DIR.parent.parent / "meta" / "lib"))
import oe.spdx_license

if len(sys.argv) < 2:
    print("Please specify a directory to run the conversion script against.")
    sys.exit(1)

LICENSE_MAP = {
    "AGPL-3": "AGPL-3.0-only",
    "AGPL-3+": "AGPL-3.0-or-later",
    "AGPLv3": "AGPL-3.0-only",
    "AGPLv3+": "AGPL-3.0-or-later",
    "AGPLv3.0": "AGPL-3.0-only",
    "AGPLv3.0+": "AGPL-3.0-or-later",
    "AGPL-3.0": "AGPL-3.0-only",
    "AGPL-3.0+": "AGPL-3.0-or-later",
    "BSD-0-Clause": "0BSD",
    "GPL-1": "GPL-1.0-only",
    "GPL-1+": "GPL-1.0-or-later",
    "GPLv1": "GPL-1.0-only",
    "GPLv1+": "GPL-1.0-or-later",
    "GPLv1.0": "GPL-1.0-only",
    "GPLv1.0+": "GPL-1.0-or-later",
    "GPL-1.0": "GPL-1.0-only",
    "GPL-1.0+": "GPL-1.0-or-later",
    "GPL-2": "GPL-2.0-only",
    "GPL-2+": "GPL-2.0-or-later",
    "GPLv2": "GPL-2.0-only",
    "GPLv2+": "GPL-2.0-or-later",
    "GPLv2.0": "GPL-2.0-only",
    "GPLv2.0+": "GPL-2.0-or-later",
    "GPL-2.0": "GPL-2.0-only",
    "GPL-2.0+": "GPL-2.0-or-later",
    "GPL-3": "GPL-3.0-only",
    "GPL-3+": "GPL-3.0-or-later",
    "GPLv3": "GPL-3.0-only",
    "GPLv3+": "GPL-3.0-or-later",
    "GPLv3.0": "GPL-3.0-only",
    "GPLv3.0+": "GPL-3.0-or-later",
    "GPL-3.0": "GPL-3.0-only",
    "GPL-3.0+": "GPL-3.0-or-later",
    "LGPLv2": "LGPL-2.0-only",
    "LGPLv2+": "LGPL-2.0-or-later",
    "LGPLv2.0": "LGPL-2.0-only",
    "LGPLv2.0+": "LGPL-2.0-or-later",
    "LGPL-2.0": "LGPL-2.0-only",
    "LGPL-2.0+": "LGPL-2.0-or-later",
    "LGPL2.1": "LGPL-2.1-only",
    "LGPL2.1+": "LGPL-2.1-or-later",
    "LGPLv2.1": "LGPL-2.1-only",
    "LGPLv2.1+": "LGPL-2.1-or-later",
    "LGPL-2.1": "LGPL-2.1-only",
    "LGPL-2.1+": "LGPL-2.1-or-later",
    "LGPLv3": "LGPL-3.0-only",
    "LGPLv3+": "LGPL-3.0-or-later",
    "LGPL-3.0": "LGPL-3.0-only",
    "LGPL-3.0+": "LGPL-3.0-or-later",
    "MPL-1": "MPL-1.0",
    "MPLv1": "MPL-1.0",
    "MPLv1.1": "MPL-1.1",
    "MPLv2": "MPL-2.0",
    "MIT-X": "MIT",
    "MIT-style": "MIT",
    "openssl": "OpenSSL",
    "PSF": "PSF-2.0",
    "PSFv2": "PSF-2.0",
    "Python-2": "Python-2.0",
    "Apachev2": "Apache-2.0",
    "Apache-2": "Apache-2.0",
    "Artisticv1": "Artistic-1.0",
    "Artistic-1": "Artistic-1.0",
    "AFL-2": "AFL-2.0",
    "AFL-1": "AFL-1.2",
    "AFLv2": "AFL-2.0",
    "AFLv1": "AFL-1.2",
    "CDDLv1": "CDDL-1.0",
    "CDDL-1": "CDDL-1.0",
    "EPLv1.0": "EPL-1.0",
    "FreeType": "FTL",
    "Nauman": "Naumen",
    "tcl": "TCL",
    "vim": "Vim",
    "SGIv1": "SGI-OpenGL",
    "Apache-2.0-with-LLVM-exception": "Apache-2.0 WITH LLVM-exception",
    "GPL-3-with-bison-exception": "GPL-3.0-or-later WITH Bison-exception-2.2",
    "GPL-2.0-with-Linux-syscall-note": "GPL-2.0-only WITH Linux-syscall-note",
}


LINE_RE = re.compile(r'^(?P<var>LICENSE[\s:].*=.*)"(?P<expression>.*)"')


def convert_unknown(node):
    node.children = [convert_unknown(c) for c in node.children]

    if not isinstance(node, oe.spdx_license.UnknownId):
        return node

    if node.ident.startswith("$"):
        return node

    if node.ident in LICENSE_MAP:
        return oe.spdx_license.parse(LICENSE_MAP[node.ident])

    ident = re.sub(r"[^a-zA-Z0-9\.\-]", "-", node.ident)

    return oe.spdx_license.LicenseRef("LicenseRef-" + ident, ident, token=node.token)


def processfile(fn):
    # print("processing file '%s'" % fn)
    try:
        fh, abs_path = tempfile.mkstemp()
        if os.path.basename(fn) == "bitbake.conf":
            return

        modified = False
        with os.fdopen(fh, "w") as new_file:
            with open(fn, "r") as old_file:
                for lineno, line in enumerate(old_file):
                    m = LINE_RE.match(line)
                    if m is None:
                        new_file.write(line)
                        continue

                    if "[doc]" in m.group("var"):
                        new_file.write(line)
                        continue

                    expression = (
                        m.group("expression").replace("|", " OR ").replace("&", " AND ")
                    ).strip()
                    if not expression or expression == "CLOSED":
                        new_file.write(line)
                        continue

                    try:
                        t = oe.spdx_license.parse(expression, allow_unknown=True)
                        if t is None:
                            new_file.write(line)
                            continue
                        t = convert_unknown(t)
                    except oe.spdx_license.ParseError as e:
                        print(f"Cannot convert {fn}:{lineno + 1}")
                        print(e.format())
                        new_file.write(line)
                        continue

                    if t.to_string() == expression:
                        new_file.write(line)
                        continue

                    new_line = m.group("var") + '"' + t.sort().to_string() + '"\n'
                    new_file.write(new_line)
                    modified = True
        new_file.close()
        if modified:
            shutil.copymode(fn, abs_path)
            os.remove(fn)
            shutil.move(abs_path, fn)
    except UnicodeDecodeError:
        pass


ourname = os.path.basename(sys.argv[0])
ourversion = "0.02"

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
            if (
                "/.git/" in fn
                or fn.endswith(".html")
                or fn.endswith(".patch")
                or fn.endswith(".m4")
                or fn.endswith(".diff")
                or fn.endswith(".orig")
            ):
                continue
            processfile(fn)

print("All files processed with version %s" % ourversion)
