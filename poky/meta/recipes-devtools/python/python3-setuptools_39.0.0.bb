require python-setuptools.inc
inherit setuptools3

do_install_append() {
    mv ${D}${bindir}/easy_install ${D}${bindir}/easy3_install
}
