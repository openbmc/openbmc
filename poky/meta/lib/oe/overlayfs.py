#
# SPDX-License-Identifier: GPL-2.0-only
#
# This file contains common functions for overlayfs and its QA check

# this function is based on https://github.com/systemd/systemd/blob/main/src/basic/unit-name.c
def escapeSystemdUnitName(path):
    escapeMap = {
        '/': '-',
        '-': "\\x2d",
        '\\': "\\x5d"
    }
    return "".join([escapeMap.get(c, c) for c in path.strip('/')])

def strForBash(s):
    return s.replace('\\', '\\\\')

def allOverlaysUnitName(d):
    return d.getVar('PN') + '-overlays.service'

def mountUnitName(unit):
    return escapeSystemdUnitName(unit) + '.mount'

def helperUnitName(unit):
    return escapeSystemdUnitName(unit) + '-create-upper-dir.service'

def unitFileList(d):
    fileList = []
    overlayMountPoints = d.getVarFlags("OVERLAYFS_MOUNT_POINT")

    if not overlayMountPoints:
        bb.fatal("A recipe uses overlayfs class but there is no OVERLAYFS_MOUNT_POINT set in your MACHINE configuration")

    # check that we have required mount points set first
    requiredMountPoints = d.getVarFlags('OVERLAYFS_WRITABLE_PATHS')
    for mountPoint in requiredMountPoints:
        if mountPoint not in overlayMountPoints:
            bb.fatal("Missing required mount point for OVERLAYFS_MOUNT_POINT[%s] in your MACHINE configuration" % mountPoint)

    for mountPoint in overlayMountPoints:
        mountPointList = d.getVarFlag('OVERLAYFS_WRITABLE_PATHS', mountPoint)
        if not mountPointList:
            bb.debug(1, "No mount points defined for %s flag, don't add to file list", mountPoint)
            continue
        for path in mountPointList.split():
            fileList.append(mountUnitName(path))
            fileList.append(helperUnitName(path))

    fileList.append(allOverlaysUnitName(d))

    return fileList

