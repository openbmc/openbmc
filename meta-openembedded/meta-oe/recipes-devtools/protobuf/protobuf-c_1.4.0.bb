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
LIC_FILES_CHKSUM = "file://LICENSE;md5=cb901168715f4782a2b06c3ddaefa558"

DEPENDS = "protobuf-native protobuf"

SRC_URI = "git://github.com/protobuf-c/protobuf-c.git;branch=master;protocol=https"
SRCREV = "f224ab2eeb648a818eb20687d7150a285442c907"

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
