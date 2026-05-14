HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
DESCRIPTION = "U-Boot, a boot loader for Embedded boards based on PowerPC, \
ARM, MIPS and several other processors, which can be installed in a boot \
ROM and used to initialize and test the hardware or to download and run \
application code."
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native python3-setuptools-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "88dc2788777babfd6322fa655df549a019aa1e69"

SRC_URI = "git://source.denx.de/u-boot/u-boot.git;protocol=https;branch=master;tag=v${PV}"

B = "${WORKDIR}/build"

inherit pkgconfig

do_configure[cleandirs] = "${B}"
