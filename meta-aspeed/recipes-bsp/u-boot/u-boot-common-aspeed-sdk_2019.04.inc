HOMEPAGE = "https://github.com/AspeedTech-BMC/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native xxd-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "ed3994a41fb42d2d3d18616022853a3856107885"
SRC_URI = "git://git@github.com/openbmc/u-boot.git;nobranch=1;protocol=https"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

# We don't have 'u-boot-initial-env' target in our u-boot version
UBOOT_INITIAL_ENV=""

PV = "v2019.04+git${SRCPV}"
