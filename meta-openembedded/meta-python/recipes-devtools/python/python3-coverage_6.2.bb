SUMMARY = "Code coverage measurement for Python"
HOMEPAGE = "https://coverage.readthedocs.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "e2cad8093172b7d1595b4ad66f24270808658e11acf43a8f95b41276162eb5b8"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-sqlite3 \
"
