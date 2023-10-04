SUMMARY = "[Boost::ext].SML (State Machine Language)"
DESCRIPTION = "Your scalable C++14 one header only State Machine Library with no dependencies"
HOMEPAGE = "https://github.com/boost-ext/sml"
BUGTRACKER = "https://github.com/boost-ext/sml/issues"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=e4224ccaecb14d942c71d31bef20d78c"

DEPENDS += "boost qemu-native"

PV = "1.1.9"

SRC_URI = "git://github.com/boost-ext/sml.git;protocol=https;branch=master"
SRCREV = "751b6f10b73c086e0e8850a7ef5b5aea2136831b"

S = "${WORKDIR}/git"

inherit cmake

# Building benchmarks takes a lot of time and requires additional headers (euml2.hpp)
# Building examples fail with gcc-12
EXTRA_OECMAKE:append = " -DSML_BUILD_BENCHMARKS:bool=OFF -DSML_BUILD_EXAMPLES=OFF"

# [boost::ext].XML is a header only C++ library, so the main package will be empty.
ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "nativesdk"
