require python-netaddr.inc
inherit setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-textutils \
"
