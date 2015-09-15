#
# Copyright 2006-2007 Openedhand Ltd.
#

ROOTFS_PKGMANAGE = "dpkg apt"
ROOTFS_PKGMANAGE_BOOTSTRAP  = "run-postinsts"

do_rootfs[depends] += "dpkg-native:do_populate_sysroot apt-native:do_populate_sysroot"
do_populate_sdk[depends] += "dpkg-native:do_populate_sysroot apt-native:do_populate_sysroot bzip2-native:do_populate_sysroot"
do_rootfs[recrdeptask] += "do_package_write_deb"
rootfs_deb_do_rootfs[vardepsexclude] += "BUILDNAME"
do_rootfs[vardeps] += "PACKAGE_FEED_URIS"

do_rootfs[lockfiles] += "${DEPLOY_DIR_DEB}/deb.lock"
do_populate_sdk[lockfiles] += "${DEPLOY_DIR_DEB}/deb.lock"

python rootfs_deb_bad_recommendations() {
    if d.getVar("BAD_RECOMMENDATIONS", True):
        bb.warn("Debian package install does not support BAD_RECOMMENDATIONS")
}
do_rootfs[prefuncs] += "rootfs_deb_bad_recommendations"

DEB_POSTPROCESS_COMMANDS = ""

opkglibdir = "${localstatedir}/lib/opkg"

python () {
    # Map TARGET_ARCH to Debian's ideas about architectures
    darch = d.getVar('SDK_ARCH', True)
    if darch in ["x86", "i486", "i586", "i686", "pentium"]:
         d.setVar('DEB_SDK_ARCH', 'i386')
    elif darch == "x86_64":
         d.setVar('DEB_SDK_ARCH', 'amd64')
    elif darch == "arm":
         d.setVar('DEB_SDK_ARCH', 'armel')
}

# This will of course only work after rootfs_deb_do_rootfs or populate_sdk_deb has been called
DPKG_QUERY_COMMAND = "${STAGING_BINDIR_NATIVE}/dpkg-query --admindir=$INSTALL_ROOTFS_DEB/var/lib/dpkg"
