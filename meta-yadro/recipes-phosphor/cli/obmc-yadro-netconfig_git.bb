SUMMARY = "Command line tool for configuring network"
DESCRIPTION = "YADRO OpenBMC command line tool for configuring network"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-netconfig"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

# Dependencies
DEPENDS = "sdbusplus"

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-netconfig.git"
SRCREV  = "b300465732616c6d456bdc2df2cce9d1246636a8"
