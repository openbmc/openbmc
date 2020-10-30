SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0960d52d61b0801760f39463288c2672"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

SRC_URI[md5sum] = "8c0595292fae76297eac03ae76507b7b"
SRC_URI[sha256sum] = "9b1ade2d1ba5d9b40a6d1de1d55ded4394ab8002718092ae80a08532c2add2e6"

BBCLASSEXTEND = "native nativesdk"
