inherit setuptools3
require python-pyserial.inc

do_install_append() {
    rm -f ${D}${bindir}/miniterm.py
    rmdir ${D}${bindir}
}

RDEPENDS_${PN} += "${PYTHON_PN}-enum ${PYTHON_PN}-selectors"
