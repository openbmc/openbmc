SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4e95e368cd6cb090815046688e92d11e"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

SRC_URI[sha256sum] = "2b7e22b1268c2ed85d73e5629097c9a63357f2429667ada9863cd05ff8ee33aa"

BBCLASSEXTEND = "native nativesdk"
