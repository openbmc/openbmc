SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "298ebab76e6ed6a998eabc81545ec58f5610f44e2ddb4858b539a0634093f8ce"

S = "${WORKDIR}/gspread-${PV}"

RDEPENDS:${PN} = "python3-requests"

inherit pypi python_poetry_core
