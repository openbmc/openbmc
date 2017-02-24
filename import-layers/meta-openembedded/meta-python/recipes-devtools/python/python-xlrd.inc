SUMMARY = "Library for developers to extract data from Microsoft Excel (tm) spreadsheet files"
DESCRIPTION = "Extract data from Excel spreadsheets (.xls and .xlsx,\
 versions 2.0 onwards) on any platform. Pure Python (2.6, 2.7, 3.2+). \
Strong support for Excel dates. Unicode-aware."
HOMEPAGE = "http://www.python-excel.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://xlrd/licences.py;md5=b59dace5cc1db604c4219100b0422811"

SRC_URI[md5sum] = "9a91b688cd4945477ac28187a54f9a3b"
SRC_URI[sha256sum] = "0ff87dd5d50425084f7219cb6f86bb3eb5aa29063f53d50bf270ed007e941069"

RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-io ${PYTHON_PN}-pprint ${PYTHON_PN}-shell"
