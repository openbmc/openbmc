inherit setuptools3
require python-chardet.inc

do_install_append () {
    # rename scripts that would conflict with the Python 2 build of chardet
    mv ${D}${bindir}/chardetect ${D}${bindir}/chardetect3
}
