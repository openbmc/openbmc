SUMMARY = "MessagePack implementation for C and C++"
DESCRIPTION = "MessagePack is an efficient binary serialization format. It's like JSON. but fast and small"
HOMEPAGE = "http://msgpack.org/index.html"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=7a858c074723608e08614061dc044352 \
                    file://COPYING;md5=0639c4209b6f2abf1437c813b208f2d3 \
                    file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c \
                   "

SRC_URI = "https://github.com/msgpack/msgpack-c/releases/download/cpp-${PV}/msgpack-cxx-${PV}.tar.gz"
SRC_URI[sha256sum] = "7504b7af7e7b9002ce529d4f941e1b7fb1fb435768780ce7da4abaac79bb156f"

UPSTREAM_CHECK_URI = "https://github.com/msgpack/msgpack-c/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

S = "${UNPACKDIR}/msgpack-cxx-${PV}"

DEPENDS += "boost"

inherit cmake pkgconfig

RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND += "native nativesdk"
