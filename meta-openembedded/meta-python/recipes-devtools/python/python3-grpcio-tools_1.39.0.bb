DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-grpcio"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch"

SRC_URI[sha256sum] = "39dfe7415bc0d3860fdb8dd90607594b046b88b57dbe64284efa4820f951c805"

RDEPENDS:${PN} = "${PYTHON_PN}-grpcio"

BBCLASSEXTEND = "native nativesdk"

# Needs abseil-cpp which does not build for ppc64le/musl
COMPATIBLE_HOST:libc-musl:powerpc64le = "null"

