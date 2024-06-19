DESCRIPTION = "A colored formatter for the python logging module"
HOMEPAGE = "https://github.com/borntyping/python-colorlog"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5c3c6ebdec7792ae12df8d1c0a46b26a"

inherit pypi setuptools3

PYPI_PACKAGE = "colorlog"

SRC_URI[sha256sum] = "3e3e079a41feb5a1b64f978b5ea4f46040a94f11f0e8bbb8261e3dbbeca64d44"

RDEPENDS:${PN} += "python3-logging"

BBCLASSEXTEND += "native nativesdk"
