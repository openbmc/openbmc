SUMMARY = "Python @deprecated decorator to deprecate old python classes, functions or methods."
HOMEPAGE = "https://deprecated.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=44288e26f4896bdab14072d4fa35ff01"

SRC_URI[sha256sum] = "422b6f6d859da6f2ef57857761bfb392480502a64c3028ca9bbe86085d72115d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-wrapt \
"
