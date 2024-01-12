SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "3fcef90183f15d3c9233b4caa021a83682f2b2ee678340c42d7ca7d8be98c6d1"

S = "${WORKDIR}/gspread-${PV}"

RDEPENDS:${PN} = "python3-requests"

inherit pypi python_poetry_core
