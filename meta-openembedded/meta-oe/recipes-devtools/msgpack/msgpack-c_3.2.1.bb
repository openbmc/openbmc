SUMMARY = "MessagePack implementation for C and C++"
DESCRIPTION = "MessagePack is an efficient binary serialization format. It's like JSON. but fast and small"
HOMEPAGE = "http://msgpack.org/index.html"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://NOTICE;md5=7a858c074723608e08614061dc044352 \
                    file://COPYING;md5=0639c4209b6f2abf1437c813b208f2d3 \
                    file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c \
                   "

PV .= "+git${SRCPV}"

SRC_URI = "git://github.com/msgpack/msgpack-c \
           "
# cpp-3.2.1
SRCREV = "8085ab8721090a447cf98bb802d1406ad7afe420"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
