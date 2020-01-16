SUMMARY = "Remote delta-compression library."
AUTHOR = "Martin Pool, Andrew Tridgell, Donovan Baarda, Adam Schubert"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://github.com/librsync/librsync.git"
SRCREV = "5917692418657dc78c9cbde3a8db4c85f25b9c8d"
S = "${WORKDIR}/git"

DEPENDS = "popt"

inherit cmake

PACKAGES =+ "rdiff"
FILES_rdiff = "${bindir}/rdiff"

BBCLASSEXTEND = "native nativesdk"
