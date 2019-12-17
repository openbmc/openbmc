SUMMARY = "Simple tool for input event debugging"
HOMEPAGE = "http://people.freedesktop.org/~whot/evtest/"
AUTHOR = "Vojtech Pavlik <vojtech@suse.cz>"
SECTION = "console/utils"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libxml2"

SRCREV = "16e5104127a620686bdddc4a9ad62881134d6c69"
SRC_URI = "git://anongit.freedesktop.org/evtest;protocol=git \
           file://add_missing_limits_h_include.patch"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
