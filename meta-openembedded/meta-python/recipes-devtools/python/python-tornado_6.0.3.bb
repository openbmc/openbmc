inherit pypi setuptools
require python-tornado.inc
RDEPENDS_${PN} += " \
    ${PYTHON_PN}-backports-abc \
    ${PYTHON_PN}-backports-ssl \
    ${PYTHON_PN}-singledispatch \
    ${PYTHON_PN}-subprocess \
"
