SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4e95e368cd6cb090815046688e92d11e"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

RDEPENDS_${PN} += " \
	python3-crypt \
	python3-datetime \
	python3-compression \
	python3-numbers \
	python3-io \
"

SRC_URI[sha256sum] = "791567acccc485ba76e0b84bccced2651981171de5b47d541520416f2f9f93e3"

BBCLASSEXTEND = "native nativesdk"
