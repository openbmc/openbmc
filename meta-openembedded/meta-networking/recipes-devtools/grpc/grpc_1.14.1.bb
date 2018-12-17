DESCRIPTION = "A high performance, open source, general-purpose RPC framework. \
Provides gRPC libraries for multiple languages written on top of shared C core library \
(C++, Node.js, Python, Ruby, Objective-C, PHP, C#)"
HOMEPAGE = "https://github.com/grpc/grpc"
SECTION = "libs"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "gflags c-ares protobuf protobuf-native protobuf-c protobuf-c-native openssl"
DEPENDS_append_class-target = " gtest grpc-native "
DEPENDS_append_class-nativesdk = " grpc-native "

S = "${WORKDIR}/git"
SRCREV = "d8020cb6daa87f1a3bb3b0c299bc081c4a3de1e8"
BRANCH = "v1.14.x"
SRC_URI = "git://github.com/grpc/grpc.git;protocol=https;branch=${BRANCH} \
           file://0001-CMakeLists.txt-Fix-libraries-installation-for-Linux.patch \
           "
SRC_URI_append_class-target = " file://0001-CMakeLists.txt-Fix-grpc_cpp_plugin-path-during-cross.patch"
SRC_URI_append_class-nativesdk = " file://0001-CMakeLists.txt-Fix-grpc_cpp_plugin-path-during-cross.patch"

# Fixes build with older compilers 4.8 especially on ubuntu 14.04
CXXFLAGS_append_class-native = " -Wl,--no-as-needed"

inherit cmake

EXTRA_OECMAKE = " \
    -DgRPC_CARES_PROVIDER=package \
    -DgRPC_ZLIB_PROVIDER=package \
    -DgRPC_SSL_PROVIDER=package \
    -DgRPC_PROTOBUF_PROVIDER=package \
    -DgRPC_GFLAGS_PROVIDER=package \
    -DgRPC_INSTALL=ON \
    -DCMAKE_CROSSCOMPILING=ON \
    -DBUILD_SHARED_LIBS=ON \
    "

BBCLASSEXTEND = "native nativesdk"

SYSROOT_DIRS_BLACKLIST_append_class-target = "${libdir}/cmake/grpc"

FILES_${PN}-dev += "${bindir}"
