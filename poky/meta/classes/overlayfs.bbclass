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
# The class assumes you have a data.mount systemd unit defined in your
# systemd-machine-units recipe and installed to the image.
#
# Then you can specify writable directories on a recipe base
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
# Note: the class does not support /etc directory itself, because systemd depends on it

REQUIRED_DISTRO_FEATURES += "systemd overlayfs"

inherit systemd features_check

python do_create_overlayfs_units() {
    CreateDirsUnitTemplate = """[Unit]
Description=Overlayfs directories setup
Requires={DATA_MOUNT_UNIT}
After={DATA_MOUNT_UNIT}
DefaultDependencies=no

[Service]
Type=oneshot
ExecStart=mkdir -p {DATA_MOUNT_POINT}/workdir{LOWERDIR} && mkdir -p {DATA_MOUNT_POINT}/upper{LOWERDIR}
RemainAfterExit=true
StandardOutput=journal

[Install]
WantedBy=multi-user.target
"""
    MountUnitTemplate = """[Unit]
Description=Overlayfs mount unit
Requires={CREATE_DIRS_SERVICE}
After={CREATE_DIRS_SERVICE}

[Mount]
What=overlay
Where={LOWERDIR}
Type=overlay
Options=lowerdir={LOWERDIR},upperdir={DATA_MOUNT_POINT}/upper{LOWERDIR},workdir={DATA_MOUNT_POINT}/workdir{LOWERDIR}

[Install]
WantedBy=multi-user.target
"""

    def prepareUnits(data, lower):
        from oe.overlayfs import mountUnitName, helperUnitName

        args = {
            'DATA_MOUNT_POINT': data,
            'DATA_MOUNT_UNIT': mountUnitName(data),
            'CREATE_DIRS_SERVICE': helperUnitName(lower),
            'LOWERDIR': lower,
        }

        with open(os.path.join(d.getVar('WORKDIR'), mountUnitName(lower)), 'w') as f:
            f.write(MountUnitTemplate.format(**args))

        with open(os.path.join(d.getVar('WORKDIR'), helperUnitName(lower)), 'w') as f:
            f.write(CreateDirsUnitTemplate.format(**args))

    overlayMountPoints = d.getVarFlags("OVERLAYFS_MOUNT_POINT")
    for mountPoint in overlayMountPoints:
        for lower in d.getVarFlag('OVERLAYFS_WRITABLE_PATHS', mountPoint).split():
            prepareUnits(d.getVarFlag('OVERLAYFS_MOUNT_POINT', mountPoint), lower)
}

# we need to generate file names early during parsing stage
python () {
    from oe.overlayfs import strForBash, unitFileList

    unitList = unitFileList(d)
    for unit in unitList:
        d.appendVar('SYSTEMD_SERVICE:' + d.getVar('PN'), ' ' + unit);
        d.appendVar('FILES:' + d.getVar('PN'), ' ' + strForBash(unit))

    d.setVar('OVERLAYFS_UNIT_LIST', ' '.join([strForBash(s) for s in unitList]))
}

do_install:append() {
    install -d ${D}${systemd_system_unitdir}
    for unit in ${OVERLAYFS_UNIT_LIST}; do
        install -m 0444 ${WORKDIR}/${unit} ${D}${systemd_system_unitdir}
    done
}

addtask create_overlayfs_units before do_install
