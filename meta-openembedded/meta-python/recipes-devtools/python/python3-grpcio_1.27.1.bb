DESCRIPTION = "Google gRPC"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "${PYTHON_PN}-protobuf"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch"
SRC_URI_append_class-target = " file://ppc-boringssl-support.patch \
                                file://riscv64_support.patch \
                                file://0001-Fix-build-on-riscv32.patch \
"
SRC_URI[md5sum] = "ccaf4e7eb4f031d926fb80035d193b98"
SRC_URI[sha256sum] = "a899725d34769a498ecd3be154021c4368dd22bdc69473f6ec46779696f626c4"

RDEPENDS_${PN} = "${PYTHON_PN}-protobuf \
                  ${PYTHON_PN}-setuptools \
                  ${PYTHON_PN}-six \
"

inherit setuptools3
inherit pypi

export GRPC_PYTHON_DISABLE_LIBC_COMPATIBILITY = "1"

do_compile_prepend_toolchain-clang() {
    export GRPC_PYTHON_CFLAGS='-fvisibility=hidden -fno-wrapv -fno-exceptions'
}

CLEANBROKEN = "1"

BBCLASSEXTEND = "native nativesdk"

CCACHE_DISABLE = "1"
