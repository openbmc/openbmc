DESCRIPTION = "Google gRPC tools"
HOMEPAGE = "http://www.grpc.io/"
SECTION = "devel/python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=7145f7cdd263359b62d342a02f005515"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-grpcio"

SRC_URI += "file://0001-setup.py-Do-not-mix-C-and-C-compiler-options.patch"

SRC_URI[sha256sum] = "9e2a41cba9c5a20ae299d0fdd377fe231434fa04cbfbfb3807293c6ec10b03cf"

RDEPENDS_${PN} = "${PYTHON_PN}-grpcio"

BBCLASSEXTEND = "native nativesdk"
