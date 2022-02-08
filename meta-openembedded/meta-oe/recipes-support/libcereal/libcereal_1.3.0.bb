SUMMARY = "A C++11 library for serialization"
HOMEPAGE = "https://uscilab.github.io/cereal/"

SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e612690af2f575dfd02e2e91443cea23"

SRCREV = "02eace19a99ce3cd564ca4e379753d69af08c2c8"
SRC_URI = "git://github.com/USCiLab/cereal.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DJUST_INSTALL_CEREAL=ON"

ALLOW_EMPTY_${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
