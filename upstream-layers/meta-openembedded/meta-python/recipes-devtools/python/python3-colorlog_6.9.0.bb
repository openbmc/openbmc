DESCRIPTION = "A colored formatter for the python logging module"
HOMEPAGE = "https://github.com/borntyping/python-colorlog"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5c3c6ebdec7792ae12df8d1c0a46b26a"

inherit pypi setuptools3

PYPI_PACKAGE = "colorlog"

SRC_URI[sha256sum] = "bfba54a1b93b94f54e1f4fe48395725a3d92fd2a4af702f6bd70946bdc0c6ac2"

RDEPENDS:${PN} += "python3-logging"

BBCLASSEXTEND += "native nativesdk"
