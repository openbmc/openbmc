SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0960d52d61b0801760f39463288c2672"

inherit pypi

PYPI_PACKAGE = "XlsxWriter"

BBCLASSEXTEND = "native nativesdk"
