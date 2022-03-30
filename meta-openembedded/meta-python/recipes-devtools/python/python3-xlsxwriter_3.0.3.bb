SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2b9a26d1a52d2c66df334bbdad23896a"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

RDEPENDS:${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-compression \
	python3-numbers \
	python3-io \
"

SRC_URI[sha256sum] = "e89f4a1d2fa2c9ea15cde77de95cd3fd8b0345d0efb3964623f395c8c4988b7f"

BBCLASSEXTEND = "native nativesdk"
