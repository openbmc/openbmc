inherit setuptools
require python-numpy.inc

RDEPENDS_${PN}_class-target_append = " \
    ${PYTHON_PN}-subprocess \
"

do_install_append(){
        rm ${D}/${bindir}/f2py
}
