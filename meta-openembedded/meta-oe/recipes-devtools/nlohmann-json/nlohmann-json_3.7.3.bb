SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=f5f7c71504da070bcf4f090205ce1080"

SRC_URI = "git://github.com/nlohmann/json.git;nobranch=1 \
           file://0001-Templatize-basic_json-ctor-from-json_ref.patch \
           file://0001-typo-fix.patch \
           "

SRCREV = "e7b3b40b5a95bc74b9a7f662830a27c49ffc01b4"

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
