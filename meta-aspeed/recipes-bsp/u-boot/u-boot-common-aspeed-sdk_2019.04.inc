HOMEPAGE = "https://github.com/AspeedTech-BMC/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "d8ebf49e584e4e134bd8d1c445ef2da276a00e1a"

UBRANCH = "aspeed-master-v2019.04"
SRC_URI = "git://github.com/AspeedTech-BMC/u-boot;branch=${UBRANCH};protocol=https"
SRC_URI += "file://0001-aspeed-Limit-bootm-memory.patch"

S = "${WORKDIR}/git"

PV = "v2019.04+git${SRCPV}"
