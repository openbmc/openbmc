HOMEPAGE = "https://github.com/HewlettPackard/gxp-uboot"
DEPENDS += "flex-native bison-native bc-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "7e035b9bf331426919aa993a6bb1de3c837e5d3c"
UBRANCH = "v2020.10-gxp-openbmc"
SRC_URI = "git://github.com/HewlettPackard/gxp-uboot.git;branch=${UBRANCH};protocol=https"

S = "${WORKDIR}/git"

PV = "v2020.10+git${SRCPV}"
