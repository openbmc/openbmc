SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "576b72b628b251d2ee41e02b982d3c714d511d2a5aa3a88e587ed9efc4d6e752"

RDEPENDS:${PN} = " \
	python3-google-auth \
	python3-google-auth-oauthlib \
	python3-strenum \
	"

inherit pypi python_flit_core
