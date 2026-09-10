SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=764abdf30b2eadd37ce47dcbce0ea1ec"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/nghttp2-${PV}.tar.xz"

SRC_URI[sha256sum] = "e05cb1388eaca3830aded4ccf20044b6e1ac1a61411dcca11b0437c4285c8bc2"

inherit cmake manpages python3native github-releases

PACKAGECONFIG[manpages] = "-DENABLE_DOC=ON,-DENABLE_DOC=OFF"

EXTRA_OECMAKE = "-DENABLE_LIB_ONLY=ON -DENABLE_PYTHON_BINDINGS=OFF"

BBCLASSEXTEND = "native nativesdk"
