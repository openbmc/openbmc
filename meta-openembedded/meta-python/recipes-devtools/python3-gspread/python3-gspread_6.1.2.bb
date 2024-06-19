SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "b147688b8c7a18c9835d5f998997ec17c97c0470babcab17f65ac2b3a32402b7"

RDEPENDS:${PN} = " \
	python3-google-auth \
	python3-google-auth-oauthlib \
	python3-strenum \
	"

inherit pypi python_flit_core
