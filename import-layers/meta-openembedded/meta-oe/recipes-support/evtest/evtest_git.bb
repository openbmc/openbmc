SUMMARY = "Simple tool for input event debugging"
HOMEPAGE = "http://people.freedesktop.org/~whot/evtest/"
AUTHOR = "Vojtech Pavlik <vojtech@suse.cz>"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libxml2"

SRCREV = "b8343ec1124da18bdabcc04809a8731b9e39295d"
SRC_URI = "git://anongit.freedesktop.org/evtest;protocol=git"

PV = "1.32+${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
