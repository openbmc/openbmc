SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0960d52d61b0801760f39463288c2672"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

SRC_URI[md5sum] = "378642a3754fb233e418e3869cf051bb"
SRC_URI[sha256sum] = "b89002dea57bb3d4c8207f3e28ef8244bfd9e936b85d74e7dd1f97e11bb70313"

BBCLASSEXTEND = "native nativesdk"
