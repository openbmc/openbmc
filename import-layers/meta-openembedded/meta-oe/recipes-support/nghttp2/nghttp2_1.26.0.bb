SUMMARY = "HTTP/2 C Library and tools"
HOMEPAGE = "https://nghttp2.org/"
SECTION = "libs"

DEPENDS = "pkgconfig cunit zlib openssl libxml2 jansson c-ares"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "https://github.com/nghttp2/nghttp2/releases/download/v${PV}/nghttp2-${PV}.tar.bz2"
SRC_URI[md5sum] = "926f07ad3b50f38f7d8935ced04716cf"
SRC_URI[sha256sum] = "0df4229f4123b5aa96e834ebcfdffe954e93d986f0252fd10123d50c6f010983"

inherit cmake pythonnative python-dir

EXTRA_OECMAKE = ""
