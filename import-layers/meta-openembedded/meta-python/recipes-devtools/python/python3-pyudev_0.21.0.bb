require python-pyudev.inc

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-lang \
"

inherit pypi setuptools3

