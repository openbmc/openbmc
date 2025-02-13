SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a4bab8df34a9a138b4e0ca56b8559a05"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-compression \
	python3-numbers \
	python3-io \
"

SRC_URI[sha256sum] = "97618759cb264fb6a93397f660cca156ffa9561743b1823dafb60dc4474e1902"

BBCLASSEXTEND = "native nativesdk"
