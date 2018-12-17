require python-sh.inc
inherit setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-contextlib \
    ${PYTHON_PN}-lang \
    ${PYTHON_PN}-textutils \
"
