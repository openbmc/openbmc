require python-pyroute2.inc
inherit setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-subprocess \
"
