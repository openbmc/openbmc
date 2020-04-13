SUMMARY = "openpyxl is a Python library to read/write Excel 2010 xlsx/xlsm/xltx/xltm files"
DESCRIPTION = "It was born from lack of existing library to read/write natively \
from Python the Office Open XML format. All kudos to the PHPExcel team as openpyxl \
was initially based on PHPExcel."

HOMEPAGE = "http://www.python-excel.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=3baf26d8abf08632beaf913ea973cb8a"

inherit pypi setuptools3

SRC_URI[md5sum] = "b067750e51f17a1a9cc6bacfdd668218"
SRC_URI[sha256sum] = "72d1ed243972cad0b3c236230083cac00d9c72804e64a2ae93d7901aec1a8f1c"

RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-io ${PYTHON_PN}-pprint ${PYTHON_PN}-shell ${PYTHON_PN}-jdcal ${PYTHON_PN}-et-xmlfile"
