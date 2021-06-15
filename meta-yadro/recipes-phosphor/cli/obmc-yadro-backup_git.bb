SUMMARY = "Command line tool for backup configuration"
DESCRIPTION = "YADRO OpenBMC command line tool for backup and restore configuration"
HOMEPAGE = "https://github.com/YADRO-KNS/obmc-yadro-backup"
PR = "r1"
PV = "1.0+git${SRCPV}"

inherit meson

# License info
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

# Source code repository
S = "${WORKDIR}/git"
SRC_URI = "git://github.com/YADRO-KNS/obmc-yadro-backup.git"
SRCREV  = "1cb990bb16708ad488980d907387bd3e4d37eb3a"
