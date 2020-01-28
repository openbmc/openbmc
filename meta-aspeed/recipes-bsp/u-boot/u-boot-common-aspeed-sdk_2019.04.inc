HOMEPAGE = "https://github.com/AspeedTech-BMC/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "0a1334330ffe43d13eef58efdcf59a2c10c13e20"

SRC_URI = "git://git@github.com/openbmc/u-boot.git;nobranch=1;protocol=https"

S = "${WORKDIR}/git"

PV = "v2019.04+git${SRCPV}"
