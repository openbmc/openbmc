DESCRIPTION="header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE="https://github.com/taocpp/PEGTL"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dccf35ef30bf912bb07b01d469965293"

SRC_URI = "git://github.com/taocpp/PEGTL.git;protocol=https;branch=3.x"
SRCREV = "464d866d8bbf4c8ddc5b67422d09b60ad3b918fc"

inherit cmake

S = "${WORKDIR}/git"

CXXFLAGS += " -Wno-error=type-limits"
