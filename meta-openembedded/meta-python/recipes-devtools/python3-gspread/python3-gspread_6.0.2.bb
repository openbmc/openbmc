SUMMARY = "Google Spreadsheets Python API"
HOMEPAGE = "https://github.com/burnash/gspread"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=9488e21983675fa56dc05af558b83e2f"

SRC_URI[sha256sum] = "0982beeb07fa3ec4482a3aaa96ca13a1e6b427a0aca4058beab4cdc33c0cbb64"

RDEPENDS:${PN} = " \
	python3-google-auth \
	python3-google-auth-oauthlib \
	python3-strenum \
	"

inherit pypi python_flit_core
