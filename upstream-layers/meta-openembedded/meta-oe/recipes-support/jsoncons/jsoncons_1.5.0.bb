DESCRIPTION = "A C++, header-only library for constructing JSON and JSON-like data formats."
HOMEPAGE = "https://github.com/danielaparker/jsoncons"

LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6ee7f7ed2001e4cde4679fdb8926f820"

SRC_URI = "git://github.com/danielaparker/jsoncons.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "95a220dff2d927f15e1fe0c7dac2821b7134e4c9"


inherit cmake

PACKAGECONFIG ??= ""
PACKAGECONFIG[tests] = "-DJSONCONS_BUILD_TESTS=ON,-DJSONCONS_BUILD_TESTS=OFF"
