SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI = "https://files.pythonhosted.org/packages/b4/4f/033ebf34778745061b67b104ef212ed5b05fa65a530f345f88c4355fdcc3/gspread-5.10.0.tar.gz"
SRC_URI[sha256sum] = "2b6bba6dc111580170346a9bcd1893e0e8c52f67a9e537caec7b7a1e27c14435"

S = "${WORKDIR}/gspread-${PV}"

RDEPENDS:${PN} = "python3-requests"

inherit python_poetry_core
