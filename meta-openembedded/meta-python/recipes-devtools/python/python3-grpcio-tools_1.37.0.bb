DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-grpcio"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch"

SRC_URI[sha256sum] = "3ec510c1b6bfc32effc639acf9a055e72dab7a7b6757bf72f2132790d6a7cf1c"

RDEPENDS_${PN} = "${PYTHON_PN}-grpcio"

BBCLASSEXTEND = "native nativesdk"

# Needs abseil-cpp which does not build for ppc64le/musl
COMPATIBLE_HOST_libc-musl_powerpc64le = "null"

