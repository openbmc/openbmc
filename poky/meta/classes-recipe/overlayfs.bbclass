#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Class for generation of overlayfs mount units
#
# It's often desired in Embedded System design to have a read-only rootfs.
# But a lot of different applications might want to have a read-write access to
# some parts of a filesystem. It can be especially useful when your update mechanism
# overwrites the whole rootfs, but you want your application data to be preserved
# between updates. This class provides a way to achieve that by means
# of overlayfs and at the same time keeping the base rootfs read-only.
#
# Usage example.
#
# Set a mount point for a partition overlayfs is going to use as upper layer
# in your machine configuration. Underlying file system can be anything that
# is supported by overlayfs. This has to be done in your machine configuration.
# QA check fails to catch file existence if you redefine this variable in your recipe!
#
#   OVERLAYFS_MOUNT_POINT[data] ?= "/data"
#
# Per default the class assumes you have a corresponding fstab entry or systemd
# mount unit (data.mount in this case) for this mount point installed on the
# image, for instance via a wks script or the systemd-machine-units recipe.
#
# If the mount point is handled somewhere else, e.g. custom boot or preinit
# scripts or in a initramfs, then this QA check can be skipped by adding
# mount-configured to the related OVERLAYFS_QA_SKIP flag:
#
#   OVERLAYFS_QA_SKIP[data] = "mount-configured"
#
# To use the overlayfs, you just have to specify writable directories inside
# their recipe:
#
#   OVERLAYFS_WRITABLE_PATHS[data] = "/usr/share/my-custom-application"
#
# To support several mount points you can use a different variable flag. Assume we
# want to have a writable location on the file system, but not interested where the data
# survive a reboot. Then we could have a mnt-overlay.mount unit for a tmpfs file system:
#
#   OVERLAYFS_MOUNT_POINT[mnt-overlay] = "/mnt/overlay"
#   OVERLAYFS_WRITABLE_PATHS[mnt-overlay] = "/usr/share/another-application"
#
# If your recipe deploys a systemd service, then it should require and be
# started after the ${PN}-overlays.service to make sure that all overlays are
# mounted beforehand.
#
# Note: the class does not support /etc directory itself, because systemd depends on it
# For /etc directory use overlayfs-etc class

REQUIRED_DISTRO_FEATURES += "systemd overlayfs"

inherit systemd features_check

OVERLAYFS_CREATE_DIRS_TEMPLATE ??= "${COREBASE}/meta/files/overlayfs-create-dirs.service.in"
OVERLAYFS_MOUNT_UNIT_TEMPLATE ??= "${COREBASE}/meta/files/overlayfs-unit.mount.in"
OVERLAYFS_ALL_OVERLAYS_TEMPLATE ??= "${COREBASE}/meta/files/overlayfs-all-overlays.service.in"

python do_create_overlayfs_units() {
    from oe.overlayfs import mountUnitName

    with open(d.getVar("OVERLAYFS_CREATE_DIRS_TEMPLATE"), "r") as f:
        CreateDirsUnitTemplate = f.read()
    with open(d.getVar("OVERLAYFS_MOUNT_UNIT_TEMPLATE"), "r") as f:
        MountUnitTemplate = f.read()
    with open(d.getVar("OVERLAYFS_ALL_OVERLAYS_TEMPLATE"), "r") as f:
        AllOverlaysTemplate = f.read()

    def prepareUnits(data, lower):
        from oe.overlayfs import helperUnitName

        args = {
            'DATA_MOUNT_POINT': data,
            'DATA_MOUNT_UNIT': mountUnitName(data),
            'CREATE_DIRS_SERVICE': helperUnitName(lower),
            'LOWERDIR': lower,
        }

        bb.debug(1, "Generate systemd unit %s" % mountUnitName(lower))
        with open(os.path.join(d.getVar('WORKDIR'), mountUnitName(lower)), 'w') as f:
            f.write(MountUnitTemplate.format(**args))

        bb.debug(1, "Generate helper systemd unit %s" % helperUnitName(lower))
        with open(os.path.join(d.getVar('WORKDIR'), helperUnitName(lower)), 'w') as f:
            f.write(CreateDirsUnitTemplate.format(**args))

    def prepareGlobalUnit(dependentUnits):
        from oe.overlayfs import allOverlaysUnitName
        args = {
            'ALL_OVERLAYFS_UNITS': " ".join(dependentUnits),
            'PN': d.getVar('PN')
        }

        bb.debug(1, "Generate systemd unit with all overlays %s" % allOverlaysUnitName(d))
        with open(os.path.join(d.getVar('WORKDIR'), allOverlaysUnitName(d)), 'w') as f:
            f.write(AllOverlaysTemplate.format(**args))

    mountUnitList = []
    overlayMountPoints = d.getVarFlags("OVERLAYFS_MOUNT_POINT")
    for mountPoint in overlayMountPoints:
        bb.debug(1, "Process variable flag %s" % mountPoint)
        lowerList = d.getVarFlag('OVERLAYFS_WRITABLE_PATHS', mountPoint)
        if not lowerList:
            bb.note("No mount points defined for %s flag, skipping" % (mountPoint))
            continue
        for lower in lowerList.split():
            bb.debug(1, "Prepare mount unit for %s with data mount point %s" %
                     (lower, d.getVarFlag('OVERLAYFS_MOUNT_POINT', mountPoint)))
            prepareUnits(d.getVarFlag('OVERLAYFS_MOUNT_POINT', mountPoint), lower)
            mountUnitList.append(mountUnitName(lower))

    # set up one unit, which depends on all mount units, so users can set
    # only one dependency in their units to make sure software starts
    # when all overlays are mounted
    prepareGlobalUnit(mountUnitList)
}

# we need to generate file names early during parsing stage
python () {
    from oe.overlayfs import strForBash, unitFileList

    unitList = unitFileList(d)
    for unit in unitList:
        d.appendVar('SYSTEMD_SERVICE:' + d.getVar('PN'), ' ' + unit)
        d.appendVar('FILES:' + d.getVar('PN'), ' ' +
                d.getVar('systemd_system_unitdir') + '/' + strForBash(unit))

    d.setVar('OVERLAYFS_UNIT_LIST', ' '.join([strForBash(s) for s in unitList]))
}

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    for unit in ${OVERLAYFS_UNIT_LIST}; do
        install -m 0444 ${WORKDIR}/${unit} ${D}${systemd_system_unitdir}
    done
}

do_create_overlayfs_units[vardeps] += "OVERLAYFS_WRITABLE_PATHS"
addtask create_overlayfs_units before do_install
