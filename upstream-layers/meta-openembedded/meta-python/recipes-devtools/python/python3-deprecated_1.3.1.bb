SUMMARY = "Python @deprecated decorator to deprecate old python classes, functions or methods."
HOMEPAGE = "https://deprecated.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=44288e26f4896bdab14072d4fa35ff01"

SRC_URI[sha256sum] = "b1b50e0ff0c1fddaa5708a2c6b0a6588bb09b892825ab2b214ac9ea9d92a5223"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-wrapt \
"
