DESCRIPTION = "Google gRPC"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 & BSD-3-Clause & MPL-2.0 & MIT & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=731e401b36f8077ae0c134b59be5c906 \
    file://third_party/utf8_range/utf8_validity.h;beginline=1;endline=5;md5=db08ddb5817e660489678e7c3653805a \
    file://third_party/xxhash/xxhash.h;beginline=1;endline=34;md5=d41d564db2353fc80a713956d85b1690 \
"

DEPENDS += "abseil-cpp c-ares openssl python3-protobuf re2 zlib"

SRC_URI += "file://0001-python-enable-unbundled-cross-compilation.patch"
SRC_URI[sha256sum] = "8d1584a68d5922330025881e63a6c1b54cc8117291d382e4fa69339b6d914c56"

RDEPENDS:${PN} = "python3-protobuf"

inherit setuptools3
inherit pypi

CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

export GRPC_PYTHON_BUILD_SYSTEM_ABSL = "1"
export GRPC_PYTHON_BUILD_SYSTEM_CARES = "1"
export GRPC_PYTHON_BUILD_SYSTEM_OPENSSL = "1"
export GRPC_PYTHON_BUILD_SYSTEM_RE2 = "1"
export GRPC_PYTHON_BUILD_SYSTEM_ZLIB = "1"

GRPC_CFLAGS ?= ""
GRPC_CFLAGS:append:toolchain-clang = " -fvisibility=hidden -fno-wrapv -fno-exceptions"
export GRPC_PYTHON_CFLAGS = "${GRPC_CFLAGS}"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native nativesdk"

CCACHE_DISABLE = "1"
