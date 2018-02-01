SUMMARY = "Apache Avro data serialization system."
HOMEPAGE = "http://apr.apache.org/"
SECTION = "libs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=73bdf70f268f0b3b9c5a83dd7a6f3324"

DEPENDS = "jansson zlib xz"
PV .= "+git${SRCPV}"

SRCREV = "4b3677c32b879e0e7f717eb95f9135ac654da760"
SRC_URI = "git://github.com/apache/avro \
           file://0001-avro-c-Fix-build-with-clang-compiler.patch;patchdir=../../ \
"

S = "${WORKDIR}/git/lang/c"

LDFLAGS_append_libc-uclibc = " -lm"

inherit cmake
