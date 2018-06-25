require python-pyudev.inc

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-subprocess \
"

inherit pypi setuptools

