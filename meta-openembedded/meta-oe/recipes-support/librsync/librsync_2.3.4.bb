SUMMARY = "Remote delta-compression library."

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://github.com/librsync/librsync.git;branch=master;protocol=https"
SRCREV = "e364852674780e43d578e4239128ff7014190ed3"
S = "${WORKDIR}/git"

DEPENDS = "popt"

inherit cmake

PACKAGES =+ "rdiff"
FILES:rdiff = "${bindir}/rdiff"

BBCLASSEXTEND = "native nativesdk"
