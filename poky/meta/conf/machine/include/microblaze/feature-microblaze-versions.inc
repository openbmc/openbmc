# MicroBlaze versions are defined as features, the features are setup to
# conflict with other versions as well as unavailable features for particular
# versions.
#
# Versions before v9.0 of MicroBlaze use a versioning scheme of "vX.YY.Z"
# (where Z = [abc]). For v8.* versions of MicroBlaze the revision (Z) changes
# did not affect ABI and are ignored by this tune configuration. Though this
# format is expected by GCC including the revision, but this is defaulted to
# ".a".
#
# Since v9.0 of MicroBlaze the revision (Z) part of versions was entirely
# dropped and the version scheme was changed to "vX.Y".

def microblaze_current_version(d, gcc = False):
    import re
    # find the current version, and convert it to major/minor integers
    version = None
    for t in (d.getVar("TUNE_FEATURES") or "").split():
        m = re.search("^v(\d+)\.(\d+)", t)
        if m:
            version = int(m.group(1)), int(m.group(2))
            break

    # format the version string in generic or GCC specific formats
    if version:
        if version[0] <= 8:
            return ("v%d.%02d" % version[0:2]) + (".a" if gcc else "")
        else:
            return "v%d.%d" % version[0:2]
    return ""

# MicroBlaze versions
TUNEVALID[v8.00] = "MicroBlaze version 8.00"
TUNEVALID[v8.10] = "MicroBlaze version 8.10"
TUNEVALID[v8.20] = "MicroBlaze version 8.20"
TUNEVALID[v8.30] = "MicroBlaze version 8.30"
TUNEVALID[v8.40] = "MicroBlaze version 8.40"
TUNEVALID[v8.50] = "MicroBlaze version 8.50"
TUNEVALID[v9.0]  = "MicroBlaze version 9.0"
TUNEVALID[v9.1]  = "MicroBlaze version 9.1"
TUNEVALID[v9.2]  = "MicroBlaze version 9.2"
TUNEVALID[v9.3]  = "MicroBlaze version 9.3"
TUNEVALID[v9.4]  = "MicroBlaze version 9.4"
TUNEVALID[v9.5]  = "MicroBlaze version 9.5"
TUNEVALID[v9.6]  = "MicroBlaze version 9.6"
TUNEVALID[v10.0] = "MicroBlaze version 10.0"
TUNEVALID[v11.0] = "MicroBlaze version 11.0"

# Version conflict matrix
TUNECONFLICTS[v8.00] = ""
TUNECONFLICTS[v8.10] = "v8.00"
TUNECONFLICTS[v8.20] = "v8.00 v8.10"
TUNECONFLICTS[v8.30] = "v8.00 v8.10 v8.20"
TUNECONFLICTS[v8.40] = "v8.00 v8.10 v8.20 v8.30"
TUNECONFLICTS[v8.50] = "v8.00 v8.10 v8.20 v8.30 v8.40"
TUNECONFLICTS[v9.0]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50"
TUNECONFLICTS[v9.1]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0"
TUNECONFLICTS[v9.2]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1"
TUNECONFLICTS[v9.3]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2"
TUNECONFLICTS[v9.4]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2 v9.3"
TUNECONFLICTS[v9.5]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2 v9.3 v9.4"
TUNECONFLICTS[v9.6]  = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2 v9.3 v9.4 v9.5"
TUNECONFLICTS[v10.0] = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2 v9.3 v9.4 v9.5 v9.6"
TUNECONFLICTS[v11.0] = "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2 v9.3 v9.4 v9.5 v9.6 v10.0"

# Version flags
TUNE_CCARGS += "${@'-mcpu=%s' % (microblaze_current_version(d, True)) if microblaze_current_version(d, True) != '' else ''}"
MBPKGARCH_VERSION = "${@'-%s' % (microblaze_current_version(d, True)) if microblaze_current_version(d, True) != '' else ''}"

