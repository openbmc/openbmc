DESCRIPTION="header-only library for creating parsers according to Parsing Expression Grammar"
HOMEPAGE="https://github.com/taocpp/PEGTL"
LICENSE="MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5b6701671289d12b7ffa69138832c006"

SRC_URI = "git://github.com/taocpp/PEGTL.git;protocol=https;branch=main"
SRCREV = "6d03c8decc22b4ddd51115e2170e0e82413c7104"

inherit cmake

S = "${WORKDIR}/git"

CXXFLAGS += " -Wno-error=type-limits"
