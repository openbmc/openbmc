DESCRIPTION = "A high performance, open source, general-purpose RPC framework. \
Provides gRPC libraries for multiple languages written on top of shared C core library \
(C++, Node.js, Python, Ruby, Objective-C, PHP, C#)"
HOMEPAGE = "https://github.com/grpc/grpc"
SECTION = "libs"
LICENSE = "Apache-2.0 & BSD-3-Clause & MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=731e401b36f8077ae0c134b59be5c906"

DEPENDS = "c-ares protobuf protobuf-native openssl libnsl2 abseil-cpp re2 zlib"
DEPENDS:append:class-target = " googletest grpc-native "
DEPENDS:append:class-nativesdk = " grpc-native "

PACKAGE_BEFORE_PN = "${PN}-compiler"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-dev:append:class-native = " ${PN}-compiler"
# Configuration above allows to cross-compile gRPC applications
# In order to compile applications on the target, use the dependency below
# Both dependencies are mutually exclusive
# RDEPENDS:${PN}-dev += "${PN}-compiler"

S = "${WORKDIR}/git"
SRCREV_grpc = "e5ae3b6b44bf3b64d24bfb4b4f82556239b986db"
BRANCH = "v1.60.x"
SRC_URI = "gitsm://github.com/grpc/grpc.git;protocol=https;name=grpc;branch=${BRANCH} \
           file://0001-cmake-Link-with-libatomic-on-rv32-rv64.patch \
           "
# Fixes build with older compilers 4.8 especially on ubuntu 14.04
CXXFLAGS:append:class-native = " -Wl,--no-as-needed"

inherit cmake pkgconfig

EXTRA_OECMAKE = " \
    -DgRPC_CARES_PROVIDER=package \
    -DgRPC_ZLIB_PROVIDER=package \
    -DgRPC_SSL_PROVIDER=package \
    -DgRPC_PROTOBUF_PROVIDER=package \
    -D_gRPC_PROTOBUF_PROTOC_EXECUTABLE=${STAGING_BINDIR_NATIVE}/protoc \
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

BBCLASSEXTEND = "native nativesdk"

FILES:${PN}-compiler += " \
    ${bindir} \
    ${libdir}/libgrpc_plugin_support${SOLIBS} \
    "
