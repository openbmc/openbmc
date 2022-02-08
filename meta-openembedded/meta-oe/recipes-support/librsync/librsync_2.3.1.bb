SUMMARY = "Remote delta-compression library."
AUTHOR = "Martin Pool, Andrew Tridgell, Donovan Baarda, Adam Schubert"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SRC_URI = "git://github.com/librsync/librsync.git;branch=master;protocol=https"
SRCREV = "27f738650c20fef1285f11d85a34e5094a71c06f"
S = "${WORKDIR}/git"

DEPENDS = "popt"

inherit cmake

PACKAGES =+ "rdiff"
FILES_rdiff = "${bindir}/rdiff"

BBCLASSEXTEND = "native nativesdk"
