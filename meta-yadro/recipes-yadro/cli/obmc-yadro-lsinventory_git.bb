SUMMARY = "Command line tool for printing inventory"
DESCRIPTION = "YADRO OpenBMC command line tool for printing inventory list"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-lsinventory"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

# Dependencies
DEPENDS += " \
            json-c \
            sdbusplus \
           "

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-lsinventory.git"
SRCREV  = "d0bdf0ddd192b9d41c5c5d4af9385122b08c7608"
