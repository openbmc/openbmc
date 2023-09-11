SUMMARY = "A fast JSON parser/generator for C++ with both SAX/DOM style API"
HOMEPAGE = "http://rapidjson.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=ba04aa8f65de1396a7e59d1d746c2125"

SRC_URI = "git://github.com/miloyip/rapidjson.git;branch=master;protocol=https"

SRCREV = "0ccdbf364c577803e2a751f5aededce935314313"

PV = "1.1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DRAPIDJSON_BUILD_DOC=OFF -DRAPIDJSON_BUILD_TESTS=OFF -DRAPIDJSON_BUILD_EXAMPLES=OFF"
# the install path for cmake modules etc. is hardcoded as ${prefix}/lib in
# CMakeLists.txt, which breaks the package split with multilib
EXTRA_OECMAKE += "-DLIB_INSTALL_DIR=${libdir}"

# RapidJSON is a header-only C++ library, so the main package will be empty.

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
