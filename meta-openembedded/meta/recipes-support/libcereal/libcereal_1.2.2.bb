SUMMARY = "A C++11 library for serialization"
HOMEPAGE = "https://uscilab.github.io/cereal/"

SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e612690af2f575dfd02e2e91443cea23"

SRCREV = "51cbda5f30e56c801c07fe3d3aba5d7fb9e6cca4"
SRC_URI = "git://github.com/USCiLab/cereal.git"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DJUST_INSTALL_CEREAL=ON"
