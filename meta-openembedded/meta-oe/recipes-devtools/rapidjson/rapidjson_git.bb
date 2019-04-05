SUMMARY = "A fast JSON parser/generator for C++ with both SAX/DOM style API"
HOMEPAGE = "http://rapidjson.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://license.txt;md5=ba04aa8f65de1396a7e59d1d746c2125"

SRC_URI = "git://github.com/miloyip/rapidjson.git;nobranch=1 \
           file://0001-CMake-remove-hardcoded-CMAKECONFIG_INSTALL_DIR-path.patch"

SRCREV = "6a905f9311f82d306da77bd963ec5aa5da07da9c"

PV = "1.1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DRAPIDJSON_BUILD_DOC=OFF -DRAPIDJSON_BUILD_TESTS=OFF -DRAPIDJSON_BUILD_EXAMPLES=OFF -DLIB_INSTALL_DIR:STRING=${libdir}"

# RapidJSON is a header-only C++ library, so the main package will be empty.

ALLOW_EMPTY_${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
