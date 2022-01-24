SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e5c795ec8cf90514a6d844b1d3cc3224"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-compression \
	python3-numbers \
	python3-io \
"

SRC_URI[sha256sum] = "53005f03e8eb58f061ebf41d5767c7495ee0772c2396fe26b7e0ca22fa9c2570"

BBCLASSEXTEND = "native nativesdk"
