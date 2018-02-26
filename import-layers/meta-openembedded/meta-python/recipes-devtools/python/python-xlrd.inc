SUMMARY = "Library for developers to extract data from Microsoft Excel (tm) spreadsheet files"
DESCRIPTION = "Extract data from Excel spreadsheets (.xls and .xlsx,\
 versions 2.0 onwards) on any platform. Pure Python (2.6, 2.7, 3.2+). \
Strong support for Excel dates. Unicode-aware."
HOMEPAGE = "http://www.python-excel.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=08bd814936237b6e1355e6370b24b2c1"

SRC_URI[md5sum] = "b85c9916ac755cad489a9005f5a0d390"
SRC_URI[sha256sum] = "8a21885513e6d915fe33a8ee5fdfa675433b61405ba13e2a69e62ee36828d7e2"

RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-io ${PYTHON_PN}-pprint ${PYTHON_PN}-shell"
