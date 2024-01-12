DESCRIPTION = "WavPack is a completely open audio compression format providing lossless, high-quality lossy, and a unique hybrid compression mode."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=bb5d037e3ad41a3c84c9f2d8bb65a7b4"

DEPENDS = "openssl"

SRC_URI = "git://github.com/dbry/WavPack.git;branch=master;protocol=https \
	    file://set-soversion-and-version.patch \
	    file://extract-libtool-and-convert-to-soversion.patch \
	   "

SRCREV = "e03e8e29dc618e08e7baba9636e57ba1254874ce"

S = "${WORKDIR}/git"

inherit cmake pkgconfig lib_package

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
