DESCRIPTION="header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE="https://github.com/taocpp/PEGTL"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=80cb066ab204c7fe022f1cfe0c2c6818"

SRCREV = "776fa4a1e8bda860008524f6dd9473967c8375b1"
SRC_URI = "git://git@github.com/taocpp/PEGTL.git;protocol=https;branch=master \
           file://0001-Fix-clang-warning-about-non-virtual-dtor.patch \
          "

inherit cmake

S = "${WORKDIR}/git"
