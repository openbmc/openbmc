SUMMARY = "Python @deprecated decorator to deprecate old python classes, functions or methods."
HOMEPAGE = "https://deprecated.readthedocs.io/en/latest/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=44288e26f4896bdab14072d4fa35ff01"

SRC_URI[sha256sum] = "683e561a90de76239796e6b6feac66b99030d2dd3fcf61ef996330f14bbb9b0d"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	python3-wrapt \
"
