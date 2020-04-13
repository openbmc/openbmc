SUMMARY = "Remote delta-compression library."
AUTHOR = "Martin Pool, Andrew Tridgell, Donovan Baarda, Adam Schubert"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://github.com/librsync/librsync.git"
SRCREV = "028d9432d05ba4b75239e0ba35bcb36fbfc17e35"
S = "${WORKDIR}/git"

DEPENDS = "popt"

inherit cmake

PACKAGES =+ "rdiff"
FILES_rdiff = "${bindir}/rdiff"

BBCLASSEXTEND = "native nativesdk"
