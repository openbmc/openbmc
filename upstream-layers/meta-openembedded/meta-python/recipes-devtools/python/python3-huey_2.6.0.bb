SUMMARY = "a little task queue for python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cac039fcc82f01141cc170b48f315d4"

PYPI_PACKAGE = "huey"

SRC_URI[sha256sum] = "8d11f8688999d65266af1425b831f6e3773e99415027177b8734b0ffd5e251f6"

RDEPENDS:${PN} += " \
	python3-datetime \
	python3-logging \
	python3-multiprocessing \
	python3-json \
"

inherit pypi python_setuptools_build_meta

