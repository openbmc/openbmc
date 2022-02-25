SUMMARY = "[Boost::ext].SML (State Machine Language)"
DESCRIPTION = "Your scalable C++14 one header only State Machine Library with no dependencies"
AUTHOR = "Krzysztof Jusiak"
HOMEPAGE = "https://github.com/boost-ext/sml"
BUGTRACKER = "https://github.com/boost-ext/sml/issues"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=e4224ccaecb14d942c71d31bef20d78c"

DEPENDS += "boost qemu-native"

PV = "git${SRCPV}"

SRC_URI = "git://github.com/boost-ext/sml.git;protocol=https;branch=master"
SRCREV = "bcc8dc7815c0d17ad3a8bd52b202f4e90d4d4ca1"

S = "${WORKDIR}/git"

inherit cmake

# Building benchmarks takes a lot of time and requires additional headers (euml2.hpp)
EXTRA_OECMAKE:append = " -DSML_BUILD_BENCHMARKS:bool=OFF"

# [boost::ext].XML is a header only C++ library, so the main package will be empty.
ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "nativesdk"
