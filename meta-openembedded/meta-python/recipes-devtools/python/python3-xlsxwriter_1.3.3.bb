SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0960d52d61b0801760f39463288c2672"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

SRC_URI[md5sum] = "7c7191afe3149e41dca0e17d535421fb"
SRC_URI[sha256sum] = "830cad0a88f0f95e5a8945ee082182aa68ab89e7d9725d0c32c196207634244b"

BBCLASSEXTEND = "native nativesdk"
