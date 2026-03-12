SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=764abdf30b2eadd37ce47dcbce0ea1ec"

SRC_URI = "\
    ${GITHUB_BASE_URI}/download/v${PV}/nghttp2-${PV}.tar.xz \
    file://0001-lib-CMakeLists.txt-Fix-NGHTTP2_CONFIG_INSTALL_DIR-pa.patch \
"

SRC_URI[sha256sum] = "5511d3128850e01b5b26ec92bf39df15381c767a63441438b25ad6235def902c"

inherit cmake manpages python3native github-releases

PACKAGECONFIG[manpages] = "-DENABLE_DOC=ON,-DENABLE_DOC=OFF"

EXTRA_OECMAKE = "-DENABLE_LIB_ONLY=ON -DENABLE_PYTHON_BINDINGS=OFF"

BBCLASSEXTEND = "native nativesdk"
