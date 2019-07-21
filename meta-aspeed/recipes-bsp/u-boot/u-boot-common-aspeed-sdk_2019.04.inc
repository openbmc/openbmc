HOMEPAGE = "https://github.com/AspeedTech-BMC/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "baa29d5ecc47e6b32a3422c897dcbe30e7209e2b"

UBRANCH = "aspeed-master-v2019.04"
SRC_URI = "git://github.com/AspeedTech-BMC/u-boot;branch=${UBRANCH};protocol=https"

S = "${WORKDIR}/git"

PV = "v2019.04+git${SRCPV}"
