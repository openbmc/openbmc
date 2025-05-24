SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "bc3d02d1c39e0b40bfc8035b4fec407aa71a17f343fc81cc7e3f75bfa6555de6"

RDEPENDS:${PN} = " \
	python3-google-auth \
	python3-google-auth-oauthlib \
	python3-strenum \
	"

inherit pypi python_flit_core
