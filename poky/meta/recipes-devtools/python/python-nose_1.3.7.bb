inherit setuptools
require python-nose.inc

do_install_append() {
    rm ${D}${bindir}/nosetests
}
