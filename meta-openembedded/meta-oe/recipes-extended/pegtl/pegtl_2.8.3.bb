DESCRIPTION="header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE="https://github.com/taocpp/PEGTL"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a5195f720a8d436a4148e0cb8660400"

SRCREV = "47e878ad4fd72c91253c9d47b6f17e001ca2dfcf"
SRC_URI = "git://github.com/taocpp/PEGTL.git;protocol=https;branch=2.x \
          "

inherit cmake

S = "${WORKDIR}/git"

CXXFLAGS += " -Wno-error=type-limits"
