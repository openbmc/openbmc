DESCRIPTION = "Google gRPC"
HOMEPAGE = "https://www.grpc.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0 & BSD-3-Clause & MPL-2.0 & MIT & BSD-2-Clause"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=731e401b36f8077ae0c134b59be5c906 \
    file://third_party/utf8_range/utf8_validity.h;beginline=1;endline=5;md5=db08ddb5817e660489678e7c3653805a \
    file://third_party/xxhash/xxhash.h;beginline=1;endline=34;md5=d41d564db2353fc80a713956d85b1690 \
"

DEPENDS += "c-ares openssl python3-protobuf re2 zlib"

SRC_URI += "file://0001-python-enable-unbundled-cross-compilation.patch \
           file://abseil-ppc-fixes.patch \
           "
SRC_URI[sha256sum] = "7be78388d6da1a25c0d5ec506523db58b18be22d9c37d8d3a32c08be4987bd73"

RDEPENDS:${PN} = "python3-protobuf python3-typing-extensions"

inherit setuptools3
inherit pypi

CFLAGS:append:libc-musl = " -D_LARGEFILE64_SOURCE"

# unbundling abseil-cpp needs work on dynamic linker issue
#export GRPC_PYTHON_BUILD_SYSTEM_ABSL = "1"
export GRPC_PYTHON_BUILD_SYSTEM_CARES = "1"
export GRPC_PYTHON_BUILD_SYSTEM_OPENSSL = "1"
export GRPC_PYTHON_BUILD_SYSTEM_RE2 = "1"
export GRPC_PYTHON_BUILD_SYSTEM_ZLIB = "1"

do_compile:prepend() {
    export GRPC_PYTHON_BUILD_EXT_COMPILER_JOBS="${@oe.utils.parallel_make(d, False)}"
}

GRPC_CFLAGS ?= ""
GRPC_CFLAGS:append:toolchain-clang = " -fvisibility=hidden -fno-wrapv -fno-exceptions"
export GRPC_PYTHON_CFLAGS = "${GRPC_CFLAGS}"

CLEANBROKEN = "1"

BBCLASSEXTEND = "native nativesdk"

CCACHE_DISABLE = "1"

CVE_PRODUCT += "grpc:grpc"
