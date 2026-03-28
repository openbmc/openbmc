SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=764abdf30b2eadd37ce47dcbce0ea1ec"

SRC_URI = "\
    ${GITHUB_BASE_URI}/download/v${PV}/nghttp2-${PV}.tar.xz \
    file://0001-lib-CMakeLists.txt-Fix-NGHTTP2_CONFIG_INSTALL_DIR-pa.patch \
"

SRC_URI[sha256sum] = "6abd7ab0a7f1580d5914457cb3c85eb80455657ee5119206edbd7f848c14f0b2"

inherit cmake manpages python3native github-releases

PACKAGECONFIG[manpages] = "-DENABLE_DOC=ON,-DENABLE_DOC=OFF"

EXTRA_OECMAKE = "-DENABLE_LIB_ONLY=ON -DENABLE_PYTHON_BINDINGS=OFF"

BBCLASSEXTEND = "native nativesdk"
