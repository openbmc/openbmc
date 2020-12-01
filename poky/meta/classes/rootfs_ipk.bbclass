#
# Creates a root filesystem out of IPKs
#
# This rootfs can be mounted via root-nfs or it can be put into an cramfs/jffs etc.
# See image.bbclass for a usage of this.
#

EXTRAOPKGCONFIG ?= ""
ROOTFS_PKGMANAGE = "opkg ${EXTRAOPKGCONFIG}"

do_rootfs[depends] += "opkg-native:do_populate_sysroot opkg-utils-native:do_populate_sysroot"
do_populate_sdk[depends] += "opkg-native:do_populate_sysroot opkg-utils-native:do_populate_sysroot"
do_rootfs[recrdeptask] += "do_package_write_ipk do_package_qa"
do_rootfs[vardeps] += "PACKAGE_FEED_URIS PACKAGE_FEED_BASE_PATHS PACKAGE_FEED_ARCHS"

do_rootfs[lockfiles] += "${WORKDIR}/ipk.lock"
do_populate_sdk[lockfiles] += "${WORKDIR}/ipk.lock"
do_populate_sdk_ext[lockfiles] += "${WORKDIR}/ipk.lock"

OPKG_PREPROCESS_COMMANDS = ""

OPKG_POSTPROCESS_COMMANDS = ""

OPKGLIBDIR ??= "${localstatedir}/lib"

MULTILIBRE_ALLOW_REP = "${OPKGLIBDIR}/opkg|/usr/lib/opkg"

python () {

    if d.getVar('BUILD_IMAGES_FROM_FEEDS'):
        flags = d.getVarFlag('do_rootfs', 'recrdeptask')
        flags = flags.replace("do_package_write_ipk", "")
        flags = flags.replace("do_deploy", "")
        flags = flags.replace("do_populate_sysroot", "")
        d.setVarFlag('do_rootfs', 'recrdeptask', flags)
        d.setVar('OPKG_PREPROCESS_COMMANDS', "")
        d.setVar('OPKG_POSTPROCESS_COMMANDS', '')
}
