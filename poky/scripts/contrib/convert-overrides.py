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

if len(sys.argv) < 2:
    print("Please specify a directory to run the conversion script against.")
    sys.exit(1)

# List of strings to treat as overrides
vars = ["append", "prepend", "remove"]
vars = vars + ["qemuarm", "qemux86", "qemumips", "qemuppc", "qemuriscv", "qemuall"]
vars = vars + ["genericx86", "edgerouter", "beaglebone-yocto"]
vars = vars + ["armeb", "arm", "armv5", "armv6", "armv4", "powerpc64", "aarch64", "riscv32", "riscv64", "x86", "mips64", "powerpc"]
vars = vars + ["mipsarch", "x86-x32", "mips16e", "microblaze", "e5500-64b", "mipsisa32", "mipsisa64"]
vars = vars + ["class-native", "class-target", "class-cross-canadian", "class-cross", "class-devupstream"]
vars = vars + ["tune-",  "pn-", "forcevariable"]
vars = vars + ["libc-musl", "libc-glibc", "libc-newlib","libc-baremetal"]
vars = vars + ["task-configure", "task-compile", "task-install", "task-clean", "task-image-qa", "task-rm_work", "task-image-complete", "task-populate-sdk"]
vars = vars + ["toolchain-clang", "mydistro", "nios2", "sdkmingw32", "overrideone", "overridetwo"]
vars = vars + ["linux-gnux32", "linux-muslx32", "linux-gnun32", "mingw32", "poky", "darwin", "linuxstdbase"]
vars = vars + ["linux-gnueabi", "eabi"]
vars = vars + ["virtclass-multilib", "virtclass-mcextend"]

# List of strings to treat as overrides but only with whitespace following or another override (more restricted matching).
# Handles issues with arc matching arch.
shortvars = ["arc", "mips", "mipsel", "sh4"]

# Variables which take packagenames as an override
packagevars = ["FILES", "RDEPENDS", "RRECOMMENDS", "SUMMARY", "DESCRIPTION", "RSUGGESTS", "RPROVIDES", "RCONFLICTS", "PKG", "ALLOW_EMPTY",
              "pkg_postrm", "pkg_postinst_ontarget", "pkg_postinst", "INITSCRIPT_NAME", "INITSCRIPT_PARAMS", "DEBIAN_NOAUTONAME", "ALTERNATIVE",
              "PKGE", "PKGV", "PKGR", "USERADD_PARAM", "GROUPADD_PARAM", "CONFFILES", "SYSTEMD_SERVICE", "LICENSE", "SECTION", "pkg_preinst",
              "pkg_prerm", "RREPLACES", "GROUPMEMS_PARAM", "SYSTEMD_AUTO_ENABLE", "SKIP_FILEDEPS", "PRIVATE_LIBS", "PACKAGE_ADD_METADATA",
              "INSANE_SKIP", "DEBIANNAME", "SYSTEMD_SERVICE_ESCAPED"]

# Expressions to skip if encountered, these are not overrides
skips = ["parser_append", "recipe_to_append", "extra_append", "to_remove", "show_appends", "applied_appends", "file_appends", "handle_remove"]
skips = skips + ["expanded_removes", "color_remove", "test_remove", "empty_remove", "toaster_prepend", "num_removed", "licfiles_append", "_write_append"]
skips = skips + ["no_report_remove", "test_prepend", "test_append", "multiple_append", "test_remove", "shallow_remove", "do_remove_layer", "first_append"]
skips = skips + ["parser_remove", "to_append", "no_remove", "bblayers_add_remove", "bblayers_remove", "apply_append", "is_x86", "base_dep_prepend"]
skips = skips + ["autotools_dep_prepend", "go_map_arm", "alt_remove_links", "systemd_append_file", "file_append", "process_file_darwin"]
skips = skips + ["run_loaddata_poky", "determine_if_poky_env", "do_populate_poky_src", "libc_cv_include_x86_isa_level", "test_rpm_remove", "do_install_armmultilib"]
skips = skips + ["get_appends_for_files", "test_doubleref_remove", "test_bitbakelayers_add_remove", "elf32_x86_64", "colour_remove", "revmap_remove"]
skips = skips + ["test_rpm_remove", "test_bitbakelayers_add_remove", "recipe_append_file", "log_data_removed", "recipe_append", "systemd_machine_unit_append"]
skips = skips + ["recipetool_append", "changetype_remove", "try_appendfile_wc", "test_qemux86_directdisk", "test_layer_appends", "tgz_removed"]

imagevars = ["IMAGE_CMD", "EXTRA_IMAGECMD", "IMAGE_TYPEDEP", "CONVERSION_CMD", "COMPRESS_CMD"]
packagevars = packagevars + imagevars

vars_re = {}
for exp in vars:
    vars_re[exp] = (re.compile('((^|[#\'"\s\-\+])[A-Za-z0-9_\-:${}\.]+)_' + exp), r"\1:" + exp)

shortvars_re = {}
for exp in shortvars:
    shortvars_re[exp] = (re.compile('((^|[#\'"\s\-\+])[A-Za-z0-9_\-:${}\.]+)_' + exp + '([\(\'"\s:])'), r"\1:" + exp + r"\3")

package_re = {}
for exp in packagevars:
    package_re[exp] = (re.compile('(^|[#\'"\s\-\+]+)' + exp + '_' + '([$a-z"\'\s%\[<{\\\*].)'), r"\1" + exp + r":\2")

# Other substitutions to make
subs = {
    'r = re.compile("([^:]+):\s*(.*)")' : 'r = re.compile("(^.+?):\s+(.*)")',
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
