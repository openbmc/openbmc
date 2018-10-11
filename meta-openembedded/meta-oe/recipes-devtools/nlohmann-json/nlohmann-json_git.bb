SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=9a8ae1c2d606c432a2aa2e2de15be22a"

SRC_URI = "git://github.com/nlohmann/json.git"

PV = "3.3.0+git${SRCPV}"

SRCREV = "aafad2be1f3cd259a1e79d2f6fcf267d1ede9ec7"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DJSON_BuildTests=OFF"

# nlohmann-json is a header only C++ library, so the main package will be empty.

RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

# other packages commonly reference the file directly as "json.hpp"
# create symlink to allow this usage
do_install_append() {
    ln -s nlohmann/json.hpp ${D}${includedir}/json.hpp
}
