SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=764abdf30b2eadd37ce47dcbce0ea1ec"

SRC_URI = "https://github.com/nghttp2/nghttp2/releases/download/v${PV}/nghttp2-${PV}.tar.xz"
SRC_URI[md5sum] = "dc7536d02aa7d4883c20eaf637747381"
SRC_URI[sha256sum] = "aa090b164b17f4b91fe32310a1c0edf3e97e02cd9d1524eef42d60dd1e8d47b7"

DEPENDS = "libxml2 openssl zlib jansson cunit c-ares"

inherit cmake pythonnative python-dir
