HOMEPAGE = "https://www.github.com/HewlettPackard/gxp-uboot"
DEPENDS += "flex-native bison-native bc-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "e15b2ace2d7f38d470ba3b0415d47e05281c7660"
UBRANCH = "v2019.07-gxp-openbmc"
SRC_URI = "git://github.com/HewlettPackard/gxp-uboot.git;branch=${UBRANCH}"

S = "${WORKDIR}/git"

PV = "v2019.07+git${SRCPV}"
