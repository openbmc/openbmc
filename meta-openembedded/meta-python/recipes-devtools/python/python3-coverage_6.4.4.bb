SUMMARY = "Code coverage measurement for Python"
HOMEPAGE = "https://coverage.readthedocs.io"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2ee41112a44fe7014dce33e26468ba93"

SRC_URI[sha256sum] = "e16c45b726acb780e1e6f88b286d3c10b3914ab03438f32117c4aa52d7f30d58"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-sqlite3 \
	${PYTHON_PN}-core \
	${PYTHON_PN}-pprint \
	${PYTHON_PN}-json \
	${PYTHON_PN}-xml \
	${PYTHON_PN}-crypt \
	${PYTHON_PN}-shell \
	${PYTHON_PN}-io \
"
