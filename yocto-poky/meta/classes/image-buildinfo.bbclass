#
# Writes build information to target filesystem on /etc/build
#
# Copyright (C) 2014 Intel Corporation
# Author: Alejandro Enedino Hernandez Samaniego <alejandro.hernandez@intel.com>
#
# Licensed under the MIT license, see COPYING.MIT for details
#
# Usage: add INHERIT += "image-buildinfo" to your conf file
#

# Desired variables to display 
IMAGE_BUILDINFO_VARS ?= "DISTRO DISTRO_VERSION"

# From buildhistory.bbclass
def image_buildinfo_outputvars(vars, listvars, d): 
    vars = vars.split()
    listvars = listvars.split()
    ret = ""
    for var in vars:
        value = d.getVar(var, True) or ""
        if (d.getVarFlag(var, 'type') == "list"):
            value = oe.utils.squashspaces(value)
        ret += "%s = %s\n" % (var, value)
    return ret.rstrip('\n')

# Gets git branch's status (clean or dirty)
def get_layer_git_status(path):
    f = os.popen("cd %s; git diff --stat 2>&1 | tail -n 1" % path)
    data = f.read()
    if f.close() is None:
        if len(data) != 0:
            return "-- modified"
    return ""

# Returns layer revisions along with their respective status
def get_layer_revs(d):
    layers = (d.getVar("BBLAYERS", True) or "").split()
    medadata_revs = ["%-17s = %s:%s %s" % (os.path.basename(i), \
        base_get_metadata_git_branch(i, None).strip(), \
        base_get_metadata_git_revision(i, None), \
        get_layer_git_status(i)) \
            for i in layers]
    return '\n'.join(medadata_revs)

def buildinfo_target(d):
        # Get context
        if d.getVar('BB_WORKERCONTEXT', True) != '1':
                return ""
        # Single and list variables to be read
        vars = (d.getVar("IMAGE_BUILDINFO_VARS", True) or "")
        listvars = (d.getVar("IMAGE_BUILDINFO_LVARS", True) or "")
        return image_buildinfo_outputvars(vars, listvars, d)

# Write build information to target filesystem
buildinfo () {
cat > ${IMAGE_ROOTFS}${sysconfdir}/build << END
-----------------------
Build Configuration:  |
-----------------------
${@buildinfo_target(d)}
-----------------------
Layer Revisions:      |   
-----------------------
${@get_layer_revs(d)}
END
}

IMAGE_PREPROCESS_COMMAND += "buildinfo;"
