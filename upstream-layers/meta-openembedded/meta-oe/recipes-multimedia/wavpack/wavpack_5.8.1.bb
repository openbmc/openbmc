DESCRIPTION = "WavPack is a completely open audio compression format providing lossless, high-quality lossy, and a unique hybrid compression mode."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://license.txt;md5=3a6eec695493cff01ff6f7b7888c5631"

DEPENDS = "openssl"

SRC_URI = "git://github.com/dbry/WavPack.git;branch=master;protocol=https"
SRCREV = "4827b9889665b937b6ed71b9c6c0123152cd7a02"

inherit cmake pkgconfig lib_package

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
