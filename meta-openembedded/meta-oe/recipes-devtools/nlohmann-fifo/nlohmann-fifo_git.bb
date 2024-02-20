SUMMARY = "fifo maps for c++"
HOMEPAGE = "https://github.com/nlohmann/fifo_map"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=b67209a1e36b682a8226de19d265b1e0"

SRC_URI = "git://github.com/nlohmann/fifo_map.git;branch=master;protocol=https"

PV = "1.0.0+git"

SRCREV = "d732aaf9a315415ae8fd7eb11e3a4c1f80e42a48"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

# nlohmann-fifo is a header only C++ library, so the main package will be empty.

RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

# See https://github.com/SOCI/soci/issues/984
CXXFLAGS:append:toolchain-clang:runtime-llvm = " -DCATCH_CONFIG_CPP11_NO_SHUFFLE"

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/src/fifo_map.hpp ${D}${includedir} 
}
