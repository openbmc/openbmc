SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=dd0607f896f392c8b7d0290a676efc24"

SRC_URI = "git://github.com/nlohmann/json.git;nobranch=1 \
           "

SRCREV = "db78ac1d7716f56fc9f1b030b715f872f93964e4"

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
