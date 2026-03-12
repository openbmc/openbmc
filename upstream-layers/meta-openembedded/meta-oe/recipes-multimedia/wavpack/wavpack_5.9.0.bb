DESCRIPTION = "WavPack is a completely open audio compression format providing lossless, high-quality lossy, and a unique hybrid compression mode."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=3a6eec695493cff01ff6f7b7888c5631"

DEPENDS = "openssl"

SRC_URI = "git://github.com/dbry/WavPack.git;branch=master;protocol=https;tag=${PV}"
SRCREV = "5803634a030e2a11dba602ba057b89cc34486c67"

inherit cmake pkgconfig lib_package

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
