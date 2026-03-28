SUMMARY = "Read and write PDFs with Python, powered by qpdf"
HOMEPAGE = "https://github.com/pikepdf/pikepdf"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI[sha256sum] = "ffa6c7d0b77deb3af9735e0b0cae177c897431e10d342bb171b62e5527a622b7"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "pikepdf"

CVE_PRODUCT = "pikepdf"

DEPENDS += " \
	python3-pybind11-native \
	qpdf \
"

RDEPENDS:${PN} += " \
	python3-pillow \
	python3-lxml \
"

BBCLASSEXTEND = "native nativesdk"
