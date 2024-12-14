SUMMARY = "openpyxl is a Python library to read/write Excel 2010 xlsx/xlsm/xltx/xltm files"
DESCRIPTION = "It was born from lack of existing library to read/write natively \
from Python the Office Open XML format. All kudos to the PHPExcel team as openpyxl \
was initially based on PHPExcel."

HOMEPAGE = "http://www.python-excel.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

SRC_URI[sha256sum] = "cf0e3cf56142039133628b5acffe8ef0c12bc902d2aadd3e0fe5878dc08d1050"

RDEPENDS:${PN} += "\
	python3-compression \
	python3-io \
	python3-pprint \
	python3-shell \
	python3-jdcal \
	python3-et-xmlfile \
	python3-numbers \
	python3-xml \
"
