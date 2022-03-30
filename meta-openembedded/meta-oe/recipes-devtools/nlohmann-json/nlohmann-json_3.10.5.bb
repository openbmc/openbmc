SUMMARY = "JSON for modern C++"
HOMEPAGE = "https://nlohmann.github.io/json/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.MIT;md5=f969127d7b7ed0a8a63c2bbeae002588"

CVE_PRODUCT = "json-for-modern-cpp"

SRC_URI = "git://github.com/nlohmann/json.git;nobranch=1;protocol=https \
           "

SRCREV = "4f8fba14066156b73f1189a2b8bd568bde5284c5"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DJSON_BuildTests=OFF"

# nlohmann-json is a header only C++ library, so the main package will be empty.

RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

# other packages commonly reference the file directly as "json.hpp"
# create symlink to allow this usage
do_install:append() {
    ln -s nlohmann/json.hpp ${D}${includedir}/json.hpp
}
