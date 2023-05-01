SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=12d9fac1f0049be71ab5aa4a78da02b0"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-compression \
	python3-numbers \
	python3-io \
"

SRC_URI[sha256sum] = "02913b50b74c00f165933d5da3e3a02cab4204cb4932722a1b342c5c71034122"

BBCLASSEXTEND = "native nativesdk"
