DESCRIPTION = "A high performance, open source, general-purpose RPC framework. \
Provides gRPC libraries for multiple languages written on top of shared C core library \
(C++, Node.js, Python, Ruby, Objective-C, PHP, C#)"
HOMEPAGE = "https://github.com/grpc/grpc"
SECTION = "libs"
LICENSE = "Apache-2.0 & BSD-3-Clause & MPL-2.0 & MIT & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=731e401b36f8077ae0c134b59be5c906 \
    file://third_party/utf8_range/LICENSE;md5=d4974d297231477b2ff507c35d61c13c \
    file://third_party/xxhash/LICENSE;md5=cdfe7764d5685d8e08b3df302885d7f3 \
"

DEPENDS = "abseil-cpp c-ares openssl protobuf protobuf-native re2 zlib"
DEPENDS:append:class-target = " grpc-native"
DEPENDS:append:class-nativesdk = " grpc-native "

PACKAGE_BEFORE_PN = "${PN}-compiler"

RDEPENDS:${PN}-compiler = "${PN}"
RDEPENDS:${PN}-dev:append:class-native = " ${PN}-compiler"
# Configuration above allows to cross-compile gRPC applications
# In order to compile applications on the target, use the dependency below
# Both dependencies are mutually exclusive
# RDEPENDS:${PN}-dev += "${PN}-compiler"

S = "${WORKDIR}/git"
SRCREV_grpc = "6b49ae626bc9cd7033e062f89dbe0e0576b1110e"
BRANCH = "v1.68.x"
SRC_URI = "gitsm://github.com/grpc/grpc.git;protocol=https;name=grpc;branch=${BRANCH} \
           file://0001-cmake-Link-with-libatomic-on-rv32-rv64.patch \
           "

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
PACKAGECONFIG[protobuf-lite] = "-DgRPC_USE_PROTO_LITE=ON,-DgRPC_USE_PROTO_LITE=OFF"
PACKAGECONFIG[shared] = "-DBUILD_SHARED_LIBS=ON,-DBUILD_SHARED_LIBS=OFF,,"

do_configure:prepend() {
    sed -i -e "s#lib/pkgconfig/#${baselib}/pkgconfig/#g" ${S}/CMakeLists.txt
}

BBCLASSEXTEND = "native nativesdk"

FILES:${PN}-compiler += " \
    ${bindir} \
    ${libdir}/libgrpc_plugin_support${SOLIBS} \
    "
