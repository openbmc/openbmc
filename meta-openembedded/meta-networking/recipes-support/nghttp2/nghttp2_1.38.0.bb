SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=764abdf30b2eadd37ce47dcbce0ea1ec"

SRC_URI = "https://github.com/nghttp2/nghttp2/releases/download/v${PV}/nghttp2-${PV}.tar.xz"
SRC_URI[md5sum] = "45b47086ee6da8171e11887c1665f275"
SRC_URI[sha256sum] = "ef75c761858241c6b4372fa6397aa0481a984b84b7b07c4ec7dc2d7b9eee87f8"

DEPENDS = "libxml2 openssl zlib jansson cunit c-ares"

inherit cmake pythonnative python-dir
