SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "8c8bf83be676a019d3a483455d8b17b442f2acfc620172f245422ca4fc960dd0"

RDEPENDS:${PN} = " \
	${PYTHON_PN}-google-auth \
	${PYTHON_PN}-google-auth-oauthlib \
	${PYTHON_PN}-strenum \
	"

inherit pypi python_flit_core
