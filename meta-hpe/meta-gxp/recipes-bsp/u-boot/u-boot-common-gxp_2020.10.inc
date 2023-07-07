HOMEPAGE = "https://github.com/HewlettPackard/gxp-uboot"
DEPENDS += "flex-native bison-native bc-native"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
PE = "1"

# We use the revision in order to avoid having to fetch it from the
# repo during parse
SRCREV = "5aa47dcfa086fdde8093baa55fa0586e5b0ef56d"
UBRANCH = "v2020.10-gxp-openbmc"
SRC_URI = "git://github.com/HewlettPackard/gxp-uboot.git;branch=${UBRANCH};protocol=https"

S = "${WORKDIR}/git"

PV = "v2020.10+git${SRCPV}"
