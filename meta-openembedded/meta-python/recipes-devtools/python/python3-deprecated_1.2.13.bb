SUMMARY = "Python @deprecated decorator to deprecate old python classes, functions or methods."
HOMEPAGE = "https://deprecated.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=44288e26f4896bdab14072d4fa35ff01"

PYPI_PACKAGE = "Deprecated"
SRC_URI[sha256sum] = "43ac5335da90c31c24ba028af536a91d41d53f9e6901ddb021bcc572ce44e38d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-wrapt \
"
