DESCRIPTION = "Modern C++ network programming libraries."

# This library provides general purpose network functionality such as
# socket communication to agent libraries. It was designed to be merged
# into boost at some point and follows similar header library approach
# for most functionality.

SECTION = "libs"
LICENSE = "BSL-1.0 & MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"
PV = "0.13.0"

SRCREV = "31d304cdf52b485f465ada433d8905171b61cbff"
SRC_URI = "\
    git://github.com/cpp-netlib/cpp-netlib.git;protocol=https;branch=0.13-release  \
    file://a53f123040998744602f190944464af0e159ea19.patch \
"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

DEPENDS = "zlib boost openssl"

EXTRA_OECMAKE:append = " -DCPP-NETLIB_BUILD_TESTS=OFF -DCPP-NETLIB_BUILD_EXAMPLES=OFF"

do_install:append() {
    sed -i -e 's|${RECIPE_SYSROOT}||g' ${D}${libdir}/cmake/cppnetlib/cppnetlibConfig.cmake
    sed -i -e 's|${RECIPE_SYSROOT}||g' ${D}${libdir}/cmake/cppnetlib/cppnetlibTargets-noconfig.cmake
}

SKIP_RECIPE[cpp-netlib] ?= "Does not work with boost >= 1.87"
