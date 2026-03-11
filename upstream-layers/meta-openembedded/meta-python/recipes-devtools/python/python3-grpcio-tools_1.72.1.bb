DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "https://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

PYPI_PACKAGE = "grpcio_tools"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

DEPENDS += "python3-grpcio"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch \
            file://0001-protobuf-Disable-musttail-attribute-on-mips.patch \
            "
SRC_URI[sha256sum] = "a11afb21d0e960eb41f3c3053206e9271ec42579582c13193b99a0dca6be86d1"

RDEPENDS:${PN} = "python3-grpcio"

do_compile:prepend() {
    export GRPC_PYTHON_BUILD_EXT_COMPILER_JOBS="${@oe.utils.parallel_make(d, False)}"
}

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT += "grpc:grpc"
