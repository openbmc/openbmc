DESCRIPTION = "A colored formatter for the python logging module"
HOMEPAGE = "https://github.com/borntyping/python-colorlog"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5c3c6ebdec7792ae12df8d1c0a46b26a"

inherit pypi setuptools3


SRC_URI[sha256sum] = "2a7924c1dadf18b22a0eb8b06d1c7b01d5341707ec1641eb6fcc4fde0c3e8e5f"

RDEPENDS:${PN} += "python3-logging"

BBCLASSEXTEND += "native nativesdk"
