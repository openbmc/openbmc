SUMMARY = "Simple tool for input event debugging"
HOMEPAGE = "http://people.freedesktop.org/~whot/evtest/"
AUTHOR = "Vojtech Pavlik <vojtech@suse.cz>"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libxml2"

SRCREV = "ab140a2dab1547f7deb5233be6d94a388cf08b26"
SRC_URI = "git://anongit.freedesktop.org/evtest;protocol=git"

PV = "1.33+${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
