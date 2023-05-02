# Class for setting up /etc in overlayfs
#
# In order to have /etc directory in overlayfs a special handling at early boot stage is required
# The idea is to supply a custom init script that mounts /etc before launching actual init program,
# because the latter already requires /etc to be mounted
#
# The configuration must be machine specific. You should at least set these three variables:
#   OVERLAYFS_ETC_MOUNT_POINT ?= "/data"
#   OVERLAYFS_ETC_FSTYPE ?= "ext4"
#   OVERLAYFS_ETC_DEVICE ?= "/dev/mmcblk0p2"
#
# To control more mount options you should consider setting mount options:
#   OVERLAYFS_ETC_MOUNT_OPTIONS ?= "defaults"
#
# The class provides two options for /sbin/init generation
# 1. Default option is to rename original /sbin/init to /sbin/init.orig and place generated init under
#    original name, i.e. /sbin/init. It has an advantage that you won't need to change any kernel
#    parameters in order to make it work, but it poses a restriction that package-management can't
#    be used, becaause updating init manager would remove generated script
# 2. If you are would like to keep original init as is, you can set
#    OVERLAYFS_ETC_USE_ORIG_INIT_NAME = "0"
#    Then generated init will be named /sbin/preinit and you would need to extend you kernel parameters
#    manually in your bootloader configuration.
#
# Regardless which mode you choose, update and migration strategy of configuration files under /etc
# overlay is out of scope of this class

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "overlayfs-etc", "create_overlayfs_etc_preinit;", "", d)}'
IMAGE_FEATURES_CONFLICTS_overlayfs-etc = "${@ 'package-management' if bb.utils.to_boolean(d.getVar('OVERLAYFS_ETC_USE_ORIG_INIT_NAME'), True) else ''}"

OVERLAYFS_ETC_MOUNT_POINT ??= ""
OVERLAYFS_ETC_FSTYPE ??= ""
OVERLAYFS_ETC_DEVICE ??= ""
OVERLAYFS_ETC_USE_ORIG_INIT_NAME ??= "1"
OVERLAYFS_ETC_MOUNT_OPTIONS ??= "defaults"
OVERLAYFS_ETC_INIT_TEMPLATE ??= "${COREBASE}/meta/files/overlayfs-etc-preinit.sh.in"
OVERLAYFS_ETC_EXPOSE_LOWER ??= "0"

python create_overlayfs_etc_preinit() {
    overlayEtcMountPoint = d.getVar("OVERLAYFS_ETC_MOUNT_POINT")
    overlayEtcFsType = d.getVar("OVERLAYFS_ETC_FSTYPE")
    overlayEtcDevice = d.getVar("OVERLAYFS_ETC_DEVICE")

    if not overlayEtcMountPoint:
        bb.fatal("OVERLAYFS_ETC_MOUNT_POINT must be set in your MACHINE configuration")
    if not overlayEtcDevice:
        bb.fatal("OVERLAYFS_ETC_DEVICE must be set in your MACHINE configuration")
    if not overlayEtcFsType:
        bb.fatal("OVERLAYFS_ETC_FSTYPE should contain a valid file system type on {0}".format(overlayEtcDevice))

    with open(d.getVar("OVERLAYFS_ETC_INIT_TEMPLATE"), "r") as f:
        PreinitTemplate = f.read()

    useOrigInit = oe.types.boolean(d.getVar('OVERLAYFS_ETC_USE_ORIG_INIT_NAME'))
    preinitPath = oe.path.join(d.getVar("IMAGE_ROOTFS"), d.getVar("base_sbindir"), "preinit")
    initBaseName = oe.path.join(d.getVar("base_sbindir"), "init")
    origInitNameSuffix = ".orig"
    exposeLower = oe.types.boolean(d.getVar('OVERLAYFS_ETC_EXPOSE_LOWER'))

    args = {
        'OVERLAYFS_ETC_MOUNT_POINT': overlayEtcMountPoint,
        'OVERLAYFS_ETC_MOUNT_OPTIONS': d.getVar('OVERLAYFS_ETC_MOUNT_OPTIONS'),
        'OVERLAYFS_ETC_FSTYPE': overlayEtcFsType,
        'OVERLAYFS_ETC_DEVICE': overlayEtcDevice,
        'SBIN_INIT_NAME': initBaseName + origInitNameSuffix if useOrigInit else initBaseName,
        'OVERLAYFS_ETC_EXPOSE_LOWER': "true" if exposeLower else "false"
    }

    if useOrigInit:
        # rename original /sbin/init
        origInit = oe.path.join(d.getVar("IMAGE_ROOTFS"), initBaseName)
        bb.debug(1, "rootfs path %s, init path %s, test %s" % (d.getVar('IMAGE_ROOTFS'), origInit, d.getVar("IMAGE_ROOTFS")))
        bb.utils.rename(origInit, origInit + origInitNameSuffix)
        preinitPath = origInit

    with open(preinitPath, 'w') as f:
        f.write(PreinitTemplate.format(**args))
    os.chmod(preinitPath, 0o755)
}
