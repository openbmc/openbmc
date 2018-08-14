DESCRIPTION = "A high performance, open source, general-purpose RPC framework. \
Provides gRPC libraries for multiple languages written on top of shared C core library \
(C++, Node.js, Python, Ruby, Objective-C, PHP, C#)"
HOMEPAGE = "https://github.com/grpc/grpc"
SECTION = "libs"
LICENSE = "Apache-2"

DEPENDS = "gflags c-ares protobuf protobuf-native protobuf-c protobuf-c-native openssl"
DEPENDS_append_class-target = " gtest grpc-native "

LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "https://github.com/grpc/grpc/archive/v${PV}.tar.gz \
           file://0001-CMakeLists.txt-Fix-libraries-installation-for-Linux.patch \
           file://0004-CMakeLists.txt-Find-c-ares-in-target-sysroot-alone.patch \
           "
SRC_URI[md5sum] = "b565fa6787e42f4969395870c2ad436e"
SRC_URI[sha256sum] = "df9168da760fd2ee970c74c9d1b63377e0024be248deaa844e784d0df47599de"

SRC_URI_append_class-target = " file://0001-CMakeLists.txt-Fix-grpc_cpp_plugin-path-during-cross.patch"

inherit cmake

EXTRA_OECMAKE = " \
    -DgRPC_CARES_PROVIDER=package \
    -DgRPC_ZLIB_PROVIDER=package \
    -DgRPC_SSL_PROVIDER=package \
    -DgRPC_PROTOBUF_PROVIDER=package \
    -DgRPC_GFLAGS_PROVIDER=package \
    -DgRPC_INSTALL=1 \
    -DBUILD_SHARED_LIBS=ON \
    "

FILES_${PN}-dev += "${libdir}/cmake"

BBCLASSEXTEND = "native"
