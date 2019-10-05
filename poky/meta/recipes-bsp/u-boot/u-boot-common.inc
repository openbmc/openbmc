HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
DESCRIPTION = "U-Boot, a boot loader for Embedded boards based on PowerPC, \
ARM, MIPS and several other processors, which can be installed in a boot \
ROM and used to initialize and test the hardware or to download and run \
application code."
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "e5aee22e4be75e75a854ab64503fc80598bc2004"

SRC_URI = "git://git.denx.de/u-boot.git \
           file://0001-CVE-2019-13103.patch \
           file://0002-CVE-2019-13104.patch \
           file://0003-CVE-2019-13105.patch \
           file://0004-CVE-2019-13106.patch \
           file://0005-CVE-2019-14192-14193-14199.patch \
           file://0006-CVE-2019-14197-14200-14201-14202-14203-14204.patch \
           file://0007-CVE-2019-14194-14198.patch \
           file://0008-CVE-2019-14195.patch \
           file://0009-CVE-2019-14196.patch \
"

S = "${WORKDIR}/git"
