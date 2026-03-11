#
# Writes build information to target filesystem on /etc/buildinfo
#
# Copyright (C) 2014 Intel Corporation
# Author: Alejandro Enedino Hernandez Samaniego <alejandro.hernandez@intel.com>
#
# SPDX-License-Identifier: MIT
#
# Usage: add INHERIT += "image-buildinfo" to your conf file
#

# Desired variables to display 
IMAGE_BUILDINFO_VARS ?= "DISTRO DISTRO_VERSION"

# Desired location of the output file in the image.
IMAGE_BUILDINFO_FILE ??= "${sysconfdir}/buildinfo"
SDK_BUILDINFO_FILE ??= "/buildinfo"

# From buildhistory.bbclass
def image_buildinfo_outputvars(vars, d):
    vars = vars.split()
    ret = ""
    for var in vars:
        value = d.getVar(var) or ""
        if (d.getVarFlag(var, 'type') == "list"):
            value = oe.utils.squashspaces(value)
        ret += "%s = %s\n" % (var, value)
    return ret.rstrip('\n')

# Returns layer revisions along with their respective status
def get_layer_revs(d):
    revisions = oe.buildcfg.get_layer_revisions(d)
    medadata_revs = ["%-17s = %s:%s%s" % (r[1], r[2], r[3], r[4]) for r in revisions]
    return '\n'.join(medadata_revs)

def buildinfo_target(d):
        # Get context
        if d.getVar('BB_WORKERCONTEXT') != '1':
                return ""
        # Single and list variables to be read
        vars = (d.getVar("IMAGE_BUILDINFO_VARS") or "")
        return image_buildinfo_outputvars(vars, d)

python buildinfo() {
    if not d.getVar('IMAGE_BUILDINFO_FILE'):
        return
    destfile = d.expand('${BUILDINFODEST}${IMAGE_BUILDINFO_FILE}')
    bb.utils.mkdirhier(os.path.dirname(destfile))
    with open(destfile, 'w') as build:
        build.writelines((
            '''-----------------------
Build Configuration:  |
-----------------------
''',
            buildinfo_target(d),
            '''
-----------------------
Layer Revisions:      |
-----------------------
''',
            get_layer_revs(d),
            '''
'''
       ))
}

# Write build information to target filesystem
python buildinfo_image () {
    d.setVar("BUILDINFODEST", "${IMAGE_ROOTFS}")
    bb.build.exec_func("buildinfo", d)
}

python buildinfo_sdk () {
    d.setVar("BUILDINFODEST", "${SDK_OUTPUT}/${SDKPATH}")
    d.setVar("IMAGE_BUILDINFO_FILE", d.getVar("SDK_BUILDINFO_FILE"))
    bb.build.exec_func("buildinfo", d)
}

IMAGE_PREPROCESS_COMMAND += "buildinfo_image"
POPULATE_SDK_PRE_TARGET_COMMAND += "buildinfo_sdk"

