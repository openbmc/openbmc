#!/usr/bin/env python3
#
# Conversion script to rename variables to versions with improved terminology.
# Also highlights potentially problematic language and removed variables.
#
# Copyright (C) 2021 Richard Purdie
# Copyright (C) 2022 Wind River Systems, Inc.
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

renames = {
"BB_ENV_WHITELIST" : "BB_ENV_PASSTHROUGH",
"BB_ENV_EXTRAWHITE" : "BB_ENV_PASSTHROUGH_ADDITIONS",
"BB_HASHCONFIG_WHITELIST" : "BB_HASHCONFIG_IGNORE_VARS",
"BB_SETSCENE_ENFORCE_WHITELIST" : "BB_SETSCENE_ENFORCE_IGNORE_TASKS",
"BB_HASHBASE_WHITELIST" : "BB_BASEHASH_IGNORE_VARS",
"BB_HASHTASK_WHITELIST" : "BB_TASKHASH_IGNORE_TASKS",
"CVE_CHECK_PN_WHITELIST" : "CVE_CHECK_SKIP_RECIPE",
"CVE_CHECK_WHITELIST" : "CVE_CHECK_IGNORE",
"MULTI_PROVIDER_WHITELIST" : "BB_MULTI_PROVIDER_ALLOWED",
"PNBLACKLIST" : "SKIP_RECIPE",
"SDK_LOCAL_CONF_BLACKLIST" : "ESDK_LOCALCONF_REMOVE",
"SDK_LOCAL_CONF_WHITELIST" : "ESDK_LOCALCONF_ALLOW",
"SDK_INHERIT_BLACKLIST" : "ESDK_CLASS_INHERIT_DISABLE",
"SSTATE_DUPWHITELIST" : "SSTATE_ALLOW_OVERLAP_FILES",
"SYSROOT_DIRS_BLACKLIST" : "SYSROOT_DIRS_IGNORE",
"UNKNOWN_CONFIGURE_WHITELIST" : "UNKNOWN_CONFIGURE_OPT_IGNORE",
"ICECC_USER_CLASS_BL" : "ICECC_CLASS_DISABLE",
"ICECC_SYSTEM_CLASS_BL" : "ICECC_CLASS_DISABLE",
"ICECC_USER_PACKAGE_WL" : "ICECC_RECIPE_ENABLE",
"ICECC_USER_PACKAGE_BL" : "ICECC_RECIPE_DISABLE",
"ICECC_SYSTEM_PACKAGE_BL" : "ICECC_RECIPE_DISABLE",
"LICENSE_FLAGS_WHITELIST" : "LICENSE_FLAGS_ACCEPTED",
}

removed_list = [
"BB_STAMP_WHITELIST",
"BB_STAMP_POLICY",
"INHERIT_BLACKLIST",
"TUNEABI_WHITELIST",
]

context_check_list = [
"blacklist",
"whitelist",
"abort",
]

def processfile(fn):

    print("processing file '%s'" % fn)
    try:
        fh, abs_path = tempfile.mkstemp()
        modified = False
        with os.fdopen(fh, 'w') as new_file:
            with open(fn, "r") as old_file:
                lineno = 0
                for line in old_file:
                    lineno += 1
                    if not line or "BB_RENAMED_VARIABLE" in line:
                        continue
                    # Do the renames
                    for old_name, new_name in renames.items():
                        if old_name in line:
                            line = line.replace(old_name, new_name)
                            modified = True
                    # Find removed names
                    for removed_name in removed_list:
                        if removed_name in line:
                            print("%s needs further work at line %s because %s has been deprecated" % (fn, lineno, removed_name))
                    for check_word in context_check_list:
                        if re.search(check_word, line, re.IGNORECASE):
                            print("%s needs further work at line %s since it contains %s"% (fn, lineno, check_word))
                    new_file.write(line)
            new_file.close()
            if modified:
                print("*** Modified file '%s'" % (fn))
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
            if "ChangeLog" in fn or "/.git/" in fn or fn.endswith(".html") or fn.endswith(".patch") or fn.endswith(".m4") or fn.endswith(".diff") or fn.endswith(".orig"):
                continue
            processfile(fn)

print("All files processed with version %s" % ourversion)
