#
# Creates a root filesystem out of rpm packages
#

ROOTFS_PKGMANAGE = "rpm smartpm"
ROOTFS_PKGMANAGE_BOOTSTRAP = "run-postinsts"

# Add 50Meg of extra space for Smart
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("PACKAGE_INSTALL", "smartpm", " + 51200", "" ,d)}"

# Smart is python based, so be sure python-native is available to us.
EXTRANATIVEPATH += "python-native"

# opkg is needed for update-alternatives
RPMROOTFSDEPENDS = "rpm-native:do_populate_sysroot \
    rpmresolve-native:do_populate_sysroot \
    python-smartpm-native:do_populate_sysroot \
    createrepo-native:do_populate_sysroot \
    opkg-native:do_populate_sysroot"

do_rootfs[depends] += "${RPMROOTFSDEPENDS}"
do_populate_sdk[depends] += "${RPMROOTFSDEPENDS}"

do_rootfs[recrdeptask] += "do_package_write_rpm"
do_rootfs[vardeps] += "PACKAGE_FEED_URIS"

# RPM doesn't work with multiple rootfs generation at once due to collisions in the use of files 
# in ${DEPLOY_DIR_RPM}. This can be removed if package_update_index_rpm can be called concurrently
do_rootfs[lockfiles] += "${DEPLOY_DIR_RPM}/rpm.lock"
do_populate_sdk[lockfiles] += "${DEPLOY_DIR_RPM}/rpm.lock"

python () {
    if d.getVar('BUILD_IMAGES_FROM_FEEDS', True):
        flags = d.getVarFlag('do_rootfs', 'recrdeptask', True)
        flags = flags.replace("do_package_write_rpm", "")
        flags = flags.replace("do_deploy", "")
        flags = flags.replace("do_populate_sysroot", "")
        d.setVarFlag('do_rootfs', 'recrdeptask', flags)
        d.setVar('RPM_PREPROCESS_COMMANDS', '')
        d.setVar('RPM_POSTPROCESS_COMMANDS', '')

}
# Smart is python based, so be sure python-native is available to us.
EXTRANATIVEPATH += "python-native"

rpmlibdir = "/var/lib/rpm"
