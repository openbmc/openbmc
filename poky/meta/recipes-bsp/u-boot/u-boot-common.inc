HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
DESCRIPTION = "U-Boot, a boot loader for Embedded boards based on PowerPC, \
ARM, MIPS and several other processors, which can be installed in a boot \
ROM and used to initialize and test the hardware or to download and run \
application code."
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=5a7450c57ffe5ae63fd732446b988025"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "c4fddedc48f336eabc4ce3f74940e6aa372de18c"

SRC_URI = "git://git.denx.de/u-boot.git \
           file://0001-add-valid-fdt-check.patch \
           file://CVE-2021-27097-1.patch \
           file://CVE-2021-27097-2.patch \
           file://CVE-2021-27097-3.patch \
           file://CVE-2021-27097-4.patch \
           file://CVE-2021-27138-1.patch \
           file://CVE-2021-27138-2.patch \
          "

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"
