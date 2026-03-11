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
LIC_FILES_CHKSUM = "file://LICENSE;md5=bd8de4f63e06b1ccc06e9f8dc5b1aa97"

DEPENDS = "protobuf-native protobuf"

SRC_URI = "git://github.com/protobuf-c/protobuf-c.git;branch=master;protocol=https \
           file://protobuf-30.patch \
"
SRC_URI:append:class-native = " file://0001-Makefile.am-do-not-compile-the-code-which-was-genera.patch"

SRCREV = "185beed28e65494be7505b30c1afeaf199e19b23"


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

# No 64bit atomics
LDFLAGS:append:riscv32 = " -latomic"
