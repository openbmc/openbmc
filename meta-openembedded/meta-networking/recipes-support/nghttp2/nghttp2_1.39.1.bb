SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=764abdf30b2eadd37ce47dcbce0ea1ec"

+UPSTREAM_CHECK_URI = "https://github.com/nghttp2/nghttp2/releases"

SRC_URI = "https://github.com/nghttp2/nghttp2/releases/download/v${PV}/nghttp2-${PV}.tar.xz"
SRC_URI[md5sum] = "02b015cb178c46f27dd87228e33db35f"
SRC_URI[sha256sum] = "679160766401f474731fd60c3aca095f88451e3cc4709b72306e4c34cf981448"

DEPENDS = "libxml2 openssl zlib jansson cunit c-ares"

inherit cmake pythonnative python-dir
