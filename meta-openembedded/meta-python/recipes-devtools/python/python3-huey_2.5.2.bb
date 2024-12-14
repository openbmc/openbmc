SUMMARY = "a little task queue for python"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5cac039fcc82f01141cc170b48f315d4"

PYPI_PACKAGE = "huey"

SRC_URI[sha256sum] = "df33db474c05414ed40ee2110e9df692369871734da22d74ffb035a4bd74047f"

RDEPENDS:${PN} += " \
	python3-datetime \
	python3-logging \
	python3-multiprocessing \
	python3-json \
"

inherit pypi setuptools3

