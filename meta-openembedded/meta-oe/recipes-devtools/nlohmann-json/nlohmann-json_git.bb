SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=f5f7c71504da070bcf4f090205ce1080"

SRC_URI = "git://github.com/nlohmann/json.git"

PV = "3.7.0+git${SRCPV}"

SRCREV = "ea60d40f4a60a47d3be9560d8f7bc37c163fe47b"

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
