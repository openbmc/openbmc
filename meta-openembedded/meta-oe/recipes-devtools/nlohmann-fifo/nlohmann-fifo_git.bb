SUMMARY = "fifo maps for c++"
HOMEPAGE = "https://github.com/nlohmann/fifo_map"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=b67209a1e36b682a8226de19d265b1e0"

SRC_URI = "git://github.com/nlohmann/fifo_map.git;branch=master;protocol=https"

PV = "1.0.0+git${SRCPV}"

SRCREV = "0dfbf5dacbb15a32c43f912a7e66a54aae39d0f9"

UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

# nlohmann-fifo is a header only C++ library, so the main package will be empty.

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

do_install() {
    install -d ${D}${includedir}
    install -m 0644 ${S}/src/fifo_map.hpp ${D}${includedir} 
}
