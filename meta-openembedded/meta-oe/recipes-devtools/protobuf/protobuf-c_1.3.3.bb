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

SRCREV = "f20a3fa131c275a0e795d99a28f94b4dbbb5af26"

SRC_URI = "git://github.com/protobuf-c/protobuf-c.git \
           file://0001-avoid-race-condition.patch \
          "

S = "${WORKDIR}/git"

#make sure c++11 is used
CXXFLAGS += "-std=c++11"
BUILD_CXXFLAGS += "-std=c++11"

inherit autotools pkgconfig

# After several fix attempts there is still a race between generating
# t.test-full.pb.h and compiling cxx_generate_packed_data.c despite
# BUILT_SOURCES and explicit dependencies.
PARALLEL_MAKE = ""

PACKAGE_BEFORE_PN = "${PN}-compiler"

FILES_${PN}-compiler = "${bindir}"

RDEPENDS_${PN}-compiler = "protobuf-compiler"
RDEPENDS_${PN}-dev += "${PN}-compiler"

BBCLASSEXTEND = "native nativesdk"
