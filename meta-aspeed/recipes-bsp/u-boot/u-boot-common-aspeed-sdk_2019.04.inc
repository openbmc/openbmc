HOMEPAGE = "https://github.com/AspeedTech-BMC/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "869b79f73711d5a7b6c0bfa3c8888dc2583d1526"

SRC_URI = "git://git@github.com/openbmc/u-boot.git;nobranch=1;protocol=https"

S = "${WORKDIR}/git"

PV = "v2019.04+git${SRCPV}"
