SUMMARY = "Protocol Buffers - structured data serialisation mechanism"
DESCRIPTION = "This is protobuf-c, a C implementation of the Google Protocol Buffers data \
serialization format. It includes libprotobuf-c, a pure C library that \
implements protobuf encoding and decoding, and protoc-c, a code generator that \
converts Protocol Buffer .proto files to C descriptor code, based on the \
original protoc. protobuf-c formerly included an RPC implementation; that code \
has been split out into the protobuf-c-rpc project."
HOMEPAGE = "https://github.com/protobuf-c/protobuf-c"
SECTION = "console/tools"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9f725889e0d77383e26cb42b0b62cea2"

DEPENDS = "protobuf-native protobuf"

SRC_URI = "git://github.com/protobuf-c/protobuf-c.git;branch=next;protocol=https"
SRC_URI:append:class-native = " file://0001-Makefile.am-do-not-compile-the-code-which-was-genera.patch"

SRCREV = "ba4c55f0e686e0252c451f7163ab529e4b36831a"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

export PROTOC = "${STAGING_BINDIR_NATIVE}/protoc"

# After several fix attempts there is still a race between generating
# t.test-full.pb.h and compiling cxx_generate_packed_data.c despite
# BUILT_SOURCES and explicit dependencies.
PARALLEL_MAKE = ""

PACKAGE_BEFORE_PN = "${PN}-compiler"

FILES:${PN}-compiler = "${bindir}"

RDEPENDS:${PN}-compiler = "protobuf-compiler"
RDEPENDS:${PN}-dev += "${PN}-compiler"

BBCLASSEXTEND = "native nativesdk"
