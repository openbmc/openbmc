SUMMARY = "Forward a serial using TCP/IP"
AUTHOR = "Holger 'Zecke' Freyther'"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=ebb5c50ab7cab4baeffba14977030c07"
SECTION = "console/devel"
SRCREV = "00dbec2636ae0385ad028587e20e446272ff97ec"
PV = "1.1+gitr${SRCPV}"

SRC_URI = "git://github.com/freesmartphone/cornucopia.git"
S = "${WORKDIR}/git/tools/serial_forward"

inherit autotools
