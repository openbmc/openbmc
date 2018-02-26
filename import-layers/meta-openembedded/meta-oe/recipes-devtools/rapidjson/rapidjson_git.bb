SUMMARY = "A fast JSON parser/generator for C++ with both SAX/DOM style API"
HOMEPAGE = "http://rapidjson.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=ba04aa8f65de1396a7e59d1d746c2125"

SRC_URI = "git://github.com/miloyip/rapidjson.git;nobranch=1 \
           file://remove-march-native-from-CMAKE_CXX_FLAGS.patch \
"

SRCREV = "e5635fb27feab7f6e8d7b916aa20ad799045a641"

PV = "1.1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DRAPIDJSON_BUILD_DOC=OFF -DRAPIDJSON_BUILD_TESTS=OFF -DRAPIDJSON_BUILD_EXAMPLES=OFF"

# RapidJSON is a header-only C++ library, so the main package will be empty.

FILES_${PN}-dev += "${libdir}/cmake"
RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
