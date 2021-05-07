SUMMARY = "Python 2 and 3 compatibility library"
HOMEPAGE = "https://xlsxwriter.readthedocs.io"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=4e95e368cd6cb090815046688e92d11e"

inherit pypi setuptools3

PYPI_PACKAGE = "XlsxWriter"

SRC_URI[sha256sum] = "82be5a58c09bdc2ff8afc25acc815c465275239ddfc56d6e7b2a7e6c5d2e213b"

BBCLASSEXTEND = "native nativesdk"
