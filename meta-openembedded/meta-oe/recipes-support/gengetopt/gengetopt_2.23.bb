SUMMARY = "skeleton main.c generator"
DESCRIPTION = "Gengetopt is a tool to write command line option parsing code for C programs."
SECTION = "utils"
HOMEPAGE = "https://www.gnu.org/software/gengetopt/gengetopt.html"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ff95bfe019feaf92f524b73dd79e76eb"

SRC_URI = "${GNU_MIRROR}/gengetopt/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "ea165d618640dbdecb42ae4491337965"
SRC_URI[sha256sum] = "b941aec9011864978dd7fdeb052b1943535824169d2aa2b0e7eae9ab807584ac"

inherit autotools texinfo

BBCLASSEXTEND = "native nativesdk"
