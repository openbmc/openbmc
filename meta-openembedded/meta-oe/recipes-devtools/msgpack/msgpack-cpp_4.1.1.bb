SUMMARY = "MessagePack implementation for C and C++"
DESCRIPTION = "MessagePack is an efficient binary serialization format. It's like JSON. but fast and small"
HOMEPAGE = "http://msgpack.org/index.html"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=7a858c074723608e08614061dc044352 \
                    file://COPYING;md5=0639c4209b6f2abf1437c813b208f2d3 \
                    file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c \
                   "

SRC_URI = "https://github.com/msgpack/msgpack-c/releases/download/cpp-${PV}/msgpack-cxx-${PV}.tar.gz"
SRC_URI[sha256sum] = "8115c5edcf20bc1408c798a6bdaec16c1e52b1c34859d4982a0fb03300438f0b"

S = "${WORKDIR}/msgpack-cxx-${PV}"

DEPENDS += "boost"

inherit cmake pkgconfig

RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND += "native nativesdk"
