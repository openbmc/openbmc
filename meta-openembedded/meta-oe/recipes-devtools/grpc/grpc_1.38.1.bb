DESCRIPTION = "A high performance, open source, general-purpose RPC framework. \
Provides gRPC libraries for multiple languages written on top of shared C core library \
(C++, Node.js, Python, Ruby, Objective-C, PHP, C#)"
HOMEPAGE = "https://github.com/grpc/grpc"
SECTION = "libs"
LICENSE = "Apache-2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "c-ares protobuf protobuf-native protobuf-c protobuf-c-native openssl libnsl2 abseil-cpp re2"
DEPENDS:append:class-target = " googletest grpc-native "
DEPENDS:append:class-nativesdk = " grpc-native "

PACKAGE_BEFORE_PN = "${PN}-compiler"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-dev += "${PN}-compiler"

S = "${WORKDIR}/git"
SRCREV_grpc = "96b73272eadc01afb5fb45b92b408c47e4387274"
BRANCH = "v1.38.x"
SRC_URI = "git://github.com/grpc/grpc.git;protocol=https;name=grpc;branch=${BRANCH} \
           "
# Fixes build with older compilers 4.8 especially on ubuntu 14.04
CXXFLAGS:append:class-native = " -Wl,--no-as-needed"

inherit cmake pkgconfig

EXTRA_OECMAKE = " \
    -DgRPC_CARES_PROVIDER=package \
    -DgRPC_ZLIB_PROVIDER=package \
    -DgRPC_SSL_PROVIDER=package \
    -DgRPC_PROTOBUF_PROVIDER=package \
    -DgRPC_ABSL_PROVIDER=package \
    -DgRPC_RE2_PROVIDER=package \
    -DgRPC_INSTALL=ON \
    -DCMAKE_CROSSCOMPILING=ON \
    -DgRPC_INSTALL_LIBDIR=${baselib} \
    -DgRPC_INSTALL_CMAKEDIR=${baselib}/cmake/${BPN} \
    "

PACKAGECONFIG ??= "cpp shared"
PACKAGECONFIG[cpp] = "-DgRPC_BUILD_GRPC_CPP_PLUGIN=ON,-DgRPC_BUILD_GRPC_CPP_PLUGIN=OFF"
PACKAGECONFIG[csharp] = "-DgRPC_BUILD_GRPC_CSHARP_PLUGIN=ON,-DgRPC_BUILD_GRPC_CSHARP_PLUGIN=OFF"
PACKAGECONFIG[node] = "-DgRPC_BUILD_GRPC_NODE_PLUGIN=ON,-DgRPC_BUILD_GRPC_NODE_PLUGIN=OFF"
PACKAGECONFIG[objective-c] = "-DgRPC_BUILD_GRPC_OBJECTIVE_C_PLUGIN=ON,-DgRPC_BUILD_GRPC_OBJECTIVE_C_PLUGIN=OFF"
PACKAGECONFIG[php] = "-DgRPC_BUILD_GRPC_PHP_PLUGIN=ON,-DgRPC_BUILD_GRPC_PHP_PLUGIN=OFF"
PACKAGECONFIG[python] = "-DgRPC_BUILD_GRPC_PYTHON_PLUGIN=ON,-DgRPC_BUILD_GRPC_PYTHON_PLUGIN=OFF"
PACKAGECONFIG[ruby] = "-DgRPC_BUILD_GRPC_RUBY_PLUGIN=ON,-DgRPC_BUILD_GRPC_RUBY_PLUGIN=OFF"
PACKAGECONFIG[protobuf-lite] = "-DgRPC_USE_PROTO_LITE=ON,-DgRPC_USE_PROTO_LITE=OFF,protobuf-lite"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF,,"

do_configure:prepend() {
    sed -i -e "s#lib/pkgconfig/#${baselib}/pkgconfig/#g" ${S}/CMakeLists.txt
}

do_configure:prepend:mipsarch() {
    sed -i -e "s/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} rt m pthread)/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} atomic rt m pthread)/g" ${S}/CMakeLists.txt
}

do_configure:prepend:powerpc() {
    sed -i -e "s/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} rt m pthread)/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} atomic rt m pthread)/g" ${S}/CMakeLists.txt
}

do_configure:prepend:riscv64() {
    sed -i -e "s/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} rt m pthread)/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} atomic rt m pthread)/g" ${S}/CMakeLists.txt
}

do_configure:prepend:riscv32() {
    sed -i -e "s/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} rt m pthread)/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} atomic rt m pthread)/g" ${S}/CMakeLists.txt
}

do_configure:prepend:toolchain-clang:x86() {
    sed -i -e "s/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} rt m pthread)/set(_gRPC_ALLTARGETS_LIBRARIES \${CMAKE_DL_LIBS} atomic rt m pthread)/g" ${S}/CMakeLists.txt
}

BBCLASSEXTEND = "native nativesdk"

SYSROOT_DIRS_BLACKLIST:append:class-target = " ${baselib}/cmake/grpc"

FILES:${PN}-compiler += "${bindir}"
