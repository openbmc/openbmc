DESCRIPTION = "A colored formatter for the python logging module"
HOMEPAGE = "https://github.com/borntyping/python-colorlog"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5c3c6ebdec7792ae12df8d1c0a46b26a"

inherit pypi setuptools3

PYPI_PACKAGE = "colorlog"

SRC_URI[sha256sum] = "eb4ae5cb65fe7fec7773c2306061a8e63e02efc2c72eba9d27b0fa23c94f1321"

RDEPENDS:${PN} += "python3-logging"

BBCLASSEXTEND += "native nativesdk"
