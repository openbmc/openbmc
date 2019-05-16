require python-cffi.inc
inherit pypi setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-subprocess \
"
