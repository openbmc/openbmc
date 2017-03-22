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
        if (d.getVarFlag(var, 'type', True) == "list"):
            value = oe.utils.squashspaces(value)
        ret += "%s = %s\n" % (var, value)
    return ret.rstrip('\n')

# Gets git branch's status (clean or dirty)
def get_layer_git_status(path):
    import subprocess
    try:
        subprocess.check_output("cd %s; PSEUDO_UNLOAD=1 git diff --quiet --no-ext-diff" % path,
                                shell=True,
                                stderr=subprocess.STDOUT)
        return ""
    except subprocess.CalledProcessError as ex:
        # Silently treat errors as "modified", without checking for the
        # (expected) return code 1 in a modified git repo. For example, we get
        # output and a 129 return code when a layer isn't a git repo at all.
        return "-- modified"

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
python buildinfo () {
    with open(d.expand('${IMAGE_ROOTFS}${sysconfdir}/build'), 'w') as build:
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

IMAGE_PREPROCESS_COMMAND += "buildinfo;"
