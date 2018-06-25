inherit setuptools3
require python-nose.inc

do_install_append() {
    mv ${D}${bindir}/nosetests ${D}${bindir}/nosetests3
}
