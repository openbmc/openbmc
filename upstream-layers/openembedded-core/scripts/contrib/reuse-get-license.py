#! /usr/bin/env python3
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
# /// script
# dependencies = [
#   "reuse",
#   "py-spdx-license",
# ]
# ///
#
# Extracts the LICENSE for software that uses reuse. Run with any PEP 723
# complaint tool, e.g.
#
#   uv run reuse-get-license.py <project-root>

import argparse
import hashlib
import sys
import textwrap
from pathlib import Path

import py_spdx_license
import reuse.project

def main():
    parser = argparse.ArgumentParser(
        description="Generate LICENSE expression from reuse"
    )
    parser.add_argument("dir", help="Project root directory", type=Path)
    parser.add_argument(
        "--include-submodules",
        "-s",
        help="Include submodules",
        action="store_true",
    )
    parser.add_argument(
        "--include-meson-subprojects",
        "-m",
        help="Include meson subprojects",
        action="store_true",
    )
    parser.add_argument(
        "--width",
        "-w",
        type=int,
        help="Maximum line width for the output. Default is %(default)s",
        default=100,
    )

    args = parser.parse_args()

    prj = reuse.project.Project.from_directory(
        args.dir,
        include_submodules=args.include_submodules,
        include_meson_subprojects=args.include_meson_subprojects,
    )

    licenses = []
    for k in prj.licenses.keys():
        licenses.append(py_spdx_license.parse(k))

    lic = py_spdx_license.AndOp.join(licenses).sort()

    prefix = 'LICENSE = "'

    if args.width:
        lic_text = textwrap.wrap(
            f'{prefix}{lic.to_string()}"',
            width=args.width,
            subsequent_indent=" " * len(prefix),
        )
    else:
        lic_text = [prefix + lic.to_string() + '"']

    print(" \\\n".join(lic_text))

    tomls = reuse.project.Project.find_global_licensing(
        args.dir,
        include_submodules=args.include_submodules,
        include_meson_subprojects=args.include_meson_subprojects,
    )

    checksums = []

    for t in tomls:
        h = hashlib.md5()
        with open(t.path, "rb") as f:
            while b := f.read(4096):
                h.update(b)

        rel_path = Path(t.path).relative_to(args.dir)
        checksums.append(f"file://{rel_path};md5={h.hexdigest()}")

    if checksums:
        checksums.sort()
        prefix = 'LIC_FILES_CHKSUM = "'
        print(prefix + (" \\\n" + " " * len(prefix)).join(checksums) + '"')

    return 0


if __name__ == "__main__":
    sys.exit(main())
