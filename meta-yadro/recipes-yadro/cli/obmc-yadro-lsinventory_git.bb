SUMMARY = "Command line tool for printing inventory"
DESCRIPTION = "YADRO OpenBMC command line tool for printing inventory list"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-lsinventory"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit pkgconfig meson

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

# Dependencies
DEPENDS += " \
            nlohmann-json \
            sdbusplus \
           "

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-lsinventory.git;branch=master;protocol=https"
SRCREV  = "d8e25f82ff29c7b2c6d1888e9fe28307f173b078"
