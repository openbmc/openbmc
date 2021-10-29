SUMMARY = "openpyxl is a Python library to read/write Excel 2010 xlsx/xlsm/xltx/xltm files"
DESCRIPTION = "It was born from lack of existing library to read/write natively \
from Python the Office Open XML format. All kudos to the PHPExcel team as openpyxl \
was initially based on PHPExcel."

HOMEPAGE = "http://www.python-excel.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=8227180126797a0148f94f483f3e1489"

inherit pypi setuptools3

SRC_URI[sha256sum] = "40f568b9829bf9e446acfffce30250ac1fa39035124d55fc024025c41481c90f"

RDEPENDS:${PN} += "\
	${PYTHON_PN}-compression \
	${PYTHON_PN}-io \
	${PYTHON_PN}-pprint \
	${PYTHON_PN}-shell \
	${PYTHON_PN}-jdcal \
	${PYTHON_PN}-et-xmlfile \
	${PYTHON_PN}-numbers \
	${PYTHON_PN}-xml \
"
