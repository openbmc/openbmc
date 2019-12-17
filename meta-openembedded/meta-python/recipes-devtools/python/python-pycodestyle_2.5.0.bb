inherit setuptools
require python-pycodestyle.inc

RDEPENDS_${PN} += "${PYTHON_PN}-lang"

do_install_append () {
        if [ -f ${D}${bindir}/pycodestyle ]; then
                mv ${D}${bindir}/pycodestyle ${D}${bindir}/pycodestyle-2
        fi
}
