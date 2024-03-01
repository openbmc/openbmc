SUMMARY = "Python @deprecated decorator to deprecate old python classes, functions or methods."
HOMEPAGE = "https://deprecated.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=44288e26f4896bdab14072d4fa35ff01"

PYPI_PACKAGE = "Deprecated"
SRC_URI[sha256sum] = "e5323eb936458dccc2582dc6f9c322c852a775a27065ff2b0c4970b9d53d01b3"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-wrapt \
"
