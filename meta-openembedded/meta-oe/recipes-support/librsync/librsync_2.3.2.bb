SUMMARY = "Remote delta-compression library."
AUTHOR = "Martin Pool, Andrew Tridgell, Donovan Baarda, Adam Schubert"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://github.com/librsync/librsync.git;branch=master;protocol=https"
SRCREV = "42b636d2a65ab6914ea7cac50886da28192aaf9b"
S = "${WORKDIR}/git"

DEPENDS = "popt"

inherit cmake

PACKAGES =+ "rdiff"
FILES:rdiff = "${bindir}/rdiff"

BBCLASSEXTEND = "native nativesdk"
