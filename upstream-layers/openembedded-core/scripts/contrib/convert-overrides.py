#!/usr/bin/env python3
#
# Conversion script to add new override syntax to existing bitbake metadata
#
# Copyright (C) 2021 Richard Purdie
#
# SPDX-License-Identifier: GPL-2.0-only
#

#
# To use this script on a new layer you need to list the overrides the
# layer is known to use in the list below.
#
# Known constraint: Matching is 'loose' and in particular will find variable
# and function names with "_append" and "_remove" in them. Those need to be
# filtered out manually or in the skip list below.
#

import re
import os
import sys
import tempfile
import shutil
import mimetypes
import argparse

parser = argparse.ArgumentParser(description="Convert override syntax")
parser.add_argument("--override", "-o", action="append", default=[], help="Add additional strings to consider as an override (e.g. custom machines/distros")
parser.add_argument("--skip", "-s", action="append", default=[], help="Add additional string to skip and not consider an override")
parser.add_argument("--skip-ext", "-e", action="append", default=[], help="Additional file suffixes to skip when processing (e.g. '.foo')")
parser.add_argument("--package-vars", action="append", default=[], help="Additional variables to treat as package variables")
parser.add_argument("--image-vars", action="append", default=[], help="Additional variables to treat as image variables")
parser.add_argument("--short-override", action="append", default=[], help="Additional strings to treat as short overrides")
parser.add_argument("path", nargs="+", help="Paths to convert")

args = parser.parse_args()

# List of strings to treat as overrides
vars = args.override
vars += ["append", "prepend", "remove"]
vars += ["qemuarm", "qemux86", "qemumips", "qemuppc", "qemuriscv", "qemuall"]
vars += ["genericx86", "edgerouter", "beaglebone-yocto"]
vars += ["armeb", "arm", "armv5", "armv6", "armv4", "powerpc64", "aarch64", "riscv32", "riscv64", "x86", "mips64", "powerpc"]
vars += ["mipsarch", "x86-x32", "mips16e", "microblaze", "e5500-64b", "mipsisa32", "mipsisa64"]
vars += ["class-native", "class-target", "class-cross-canadian", "class-cross", "class-devupstream"]
vars += ["tune-",  "pn-", "forcevariable"]
vars += ["libc-musl", "libc-glibc", "libc-newlib","libc-baremetal"]
vars += ["task-configure", "task-compile", "task-install", "task-clean", "task-image-qa", "task-rm_work", "task-image-complete", "task-populate-sdk"]
vars += ["toolchain-clang", "mydistro", "nios2", "sdkmingw32", "overrideone", "overridetwo"]
vars += ["linux-gnux32", "linux-muslx32", "linux-gnun32", "mingw32", "poky", "darwin", "linuxstdbase"]
vars += ["linux-gnueabi", "eabi"]
vars += ["virtclass-multilib", "virtclass-mcextend"]

# List of strings to treat as overrides but only with whitespace following or another override (more restricted matching).
# Handles issues with arc matching arch.
shortvars = ["arc", "mips", "mipsel", "sh4"] + args.short_override

# Variables which take packagenames as an override
packagevars = ["FILES", "RDEPENDS", "RRECOMMENDS", "SUMMARY", "DESCRIPTION", "RSUGGESTS", "RPROVIDES", "RCONFLICTS", "PKG", "ALLOW_EMPTY",
              "pkg_postrm", "pkg_postinst_ontarget", "pkg_postinst", "INITSCRIPT_NAME", "INITSCRIPT_PARAMS", "DEBIAN_NOAUTONAME", "ALTERNATIVE",
              "PKGE", "PKGV", "PKGR", "USERADD_PARAM", "GROUPADD_PARAM", "CONFFILES", "SYSTEMD_SERVICE", "LICENSE", "SECTION", "pkg_preinst",
              "pkg_prerm", "RREPLACES", "GROUPMEMS_PARAM", "SYSTEMD_AUTO_ENABLE", "SKIP_FILEDEPS", "PRIVATE_LIBS", "PACKAGE_ADD_METADATA",
              "INSANE_SKIP", "DEBIANNAME", "SYSTEMD_SERVICE_ESCAPED"] + args.package_vars

# Expressions to skip if encountered, these are not overrides
skips = args.skip
skips += ["parser_append", "recipe_to_append", "extra_append", "to_remove", "show_appends", "applied_appends", "file_appends", "handle_remove"]
skips += ["expanded_removes", "color_remove", "test_remove", "empty_remove", "toaster_prepend", "num_removed", "licfiles_append", "_write_append"]
skips += ["no_report_remove", "test_prepend", "test_append", "multiple_append", "test_remove", "shallow_remove", "do_remove_layer", "first_append"]
skips += ["parser_remove", "to_append", "no_remove", "bblayers_add_remove", "bblayers_remove", "apply_append", "is_x86", "base_dep_prepend"]
skips += ["autotools_dep_prepend", "go_map_arm", "alt_remove_links", "systemd_append_file", "file_append", "process_file_darwin"]
skips += ["run_loaddata_poky", "determine_if_poky_env", "do_populate_poky_src", "libc_cv_include_x86_isa_level", "test_rpm_remove", "do_install_armmultilib"]
skips += ["get_appends_for_files", "test_doubleref_remove", "test_bitbakelayers_add_remove", "elf32_x86_64", "colour_remove", "revmap_remove"]
skips += ["test_rpm_remove", "test_bitbakelayers_add_remove", "recipe_append_file", "log_data_removed", "recipe_append", "systemd_machine_unit_append"]
skips += ["recipetool_append", "changetype_remove", "try_appendfile_wc", "test_qemux86_directdisk", "test_layer_appends", "tgz_removed"]

imagevars = ["IMAGE_CMD", "EXTRA_IMAGECMD", "IMAGE_TYPEDEP", "CONVERSION_CMD", "COMPRESS_CMD"] + args.image_vars
packagevars += imagevars

skip_ext = [".html", ".patch", ".m4", ".diff"] + args.skip_ext

vars_re = {}
for exp in vars:
    vars_re[exp] = (re.compile(r'((^|[#\'"\s\-\+])[A-Za-z0-9_\-:${}\.]+)_' + exp), r"\1:" + exp)

shortvars_re = {}
for exp in shortvars:
    shortvars_re[exp] = (re.compile(r'((^|[#\'"\s\-\+])[A-Za-z0-9_\-:${}\.]+)_' + exp + r'([\(\'"\s:])'), r"\1:" + exp + r"\3")

package_re = {}
for exp in packagevars:
    package_re[exp] = (re.compile(r'(^|[#\'"\s\-\+]+)' + exp + r'_' + r'([$a-z"\'\s%\[<{\\\*].)'), r"\1" + exp + r":\2")

# Other substitutions to make
subs = {
    'r = re.compile(r"([^:]+):\s*(.*)")' : 'r = re.compile(r"(^.+?):\s+(.*)")',
    "val = d.getVar('%s_%s' % (var, pkg))" : "val = d.getVar('%s:%s' % (var, pkg))",
    "f.write('%s_%s: %s\\n' % (var, pkg, encode(val)))" : "f.write('%s:%s: %s\\n' % (var, pkg, encode(val)))",
    "d.getVar('%s_%s' % (scriptlet_name, pkg))" : "d.getVar('%s:%s' % (scriptlet_name, pkg))",
    'ret.append(v + "_" + p)' : 'ret.append(v + ":" + p)',
}

def processfile(fn):
    print("processing file '%s'" % fn)
    try:
        fh, abs_path = tempfile.mkstemp()
        with os.fdopen(fh, 'w') as new_file:
            with open(fn, "r") as old_file:
                for line in old_file:
                    skip = False
                    for s in skips:
                        if s in line:
                            skip = True
                            if "ptest_append" in line or "ptest_remove" in line or "ptest_prepend" in line:
                                skip = False
                    for sub in subs:
                        if sub in line:
                            line = line.replace(sub, subs[sub])
                            skip = True
                    if not skip:
                        for pvar in packagevars:
                            line = package_re[pvar][0].sub(package_re[pvar][1], line)
                        for var in vars:
                            line = vars_re[var][0].sub(vars_re[var][1], line)
                        for shortvar in shortvars:
                            line = shortvars_re[shortvar][0].sub(shortvars_re[shortvar][1], line)
                    if "pkg_postinst:ontarget" in line:
                        line = line.replace("pkg_postinst:ontarget", "pkg_postinst_ontarget")
                    new_file.write(line)
        shutil.copymode(fn, abs_path)
        os.remove(fn)
        shutil.move(abs_path, fn)
    except UnicodeDecodeError:
        pass

ourname = os.path.basename(sys.argv[0])
ourversion = "0.9.3"

for p in args.path:
    if os.path.isfile(p):
        processfile(p)
    else:
        print("processing directory '%s'" % p)
        for root, dirs, files in os.walk(p):
            for name in files:
                if name == ourname:
                    continue
                fn = os.path.join(root, name)
                if os.path.islink(fn):
                    continue
                if "/.git/" in fn or any(fn.endswith(ext) for ext in skip_ext):
                    continue
                processfile(fn)

print("All files processed with version %s" % ourversion)
