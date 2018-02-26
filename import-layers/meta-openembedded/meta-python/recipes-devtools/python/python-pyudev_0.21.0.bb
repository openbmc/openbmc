require python-pyudev.inc

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-contextlib \
"

inherit pypi setuptools

