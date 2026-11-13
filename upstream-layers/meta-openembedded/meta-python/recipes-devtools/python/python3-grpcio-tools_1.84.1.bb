DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "https://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=6;endline=6;md5=0bcf70029ff7f40a132bf95381ee0b2d"

inherit pypi python_setuptools_build_meta cython

DEPENDS += "python3-grpcio python3-wheel-native"

SRC_URI[sha256sum] = "ca8a326df3441127d6709d992774844bb496db30b08f76ed5d1a912089e03f2c"

RDEPENDS:${PN} = "python3-grpcio"

do_configure:append() {
    sed -i 's/"Cython==/"Cython>=/' ${S}/pyproject.toml
}

do_compile:prepend() {
    export GRPC_PYTHON_BUILD_EXT_COMPILER_JOBS="${@oe.utils.parallel_make(d, False)}"
}

BBCLASSEXTEND = "native nativesdk"

CVE_PRODUCT += "grpc:grpc"
