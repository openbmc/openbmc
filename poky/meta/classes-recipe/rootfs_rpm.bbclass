#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

#
# Creates a root filesystem out of rpm packages
#

ROOTFS_PKGMANAGE = "rpm dnf"

# dnf is using our custom sysconfig module, and so will fail without these
export STAGING_INCDIR
export STAGING_LIBDIR

# Add 100Meg of extra space for dnf
IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains("PACKAGE_INSTALL", "dnf", " + 102400", "", d)}"

# Dnf is python based, so be sure python3-native is available to us.
EXTRANATIVEPATH += "python3-native"

RPMROOTFSDEPENDS = "rpm-native:do_populate_sysroot \
    dnf-native:do_populate_sysroot \
    createrepo-c-native:do_populate_sysroot"

do_rootfs[depends] += "${RPMROOTFSDEPENDS}"
do_populate_sdk[depends] += "${RPMROOTFSDEPENDS}"

do_rootfs[recrdeptask] += "do_package_write_rpm do_package_qa"
do_rootfs[vardeps] += "PACKAGE_FEED_URIS PACKAGE_FEED_BASE_PATHS PACKAGE_FEED_ARCHS"

python () {
    if d.getVar('BUILD_IMAGES_FROM_FEEDS'):
        flags = d.getVarFlag('do_rootfs', 'recrdeptask')
        flags = flags.replace("do_package_write_rpm", "")
        flags = flags.replace("do_deploy", "")
        flags = flags.replace("do_populate_sysroot", "")
        d.setVarFlag('do_rootfs', 'recrdeptask', flags)
        d.setVar('RPM_PREPROCESS_COMMANDS', '')
        d.setVar('RPM_POSTPROCESS_COMMANDS', '')

}
