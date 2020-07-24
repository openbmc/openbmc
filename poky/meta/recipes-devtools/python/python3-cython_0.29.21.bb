inherit setuptools3
require python-cython.inc

RDEPENDS_${PN} += "\
    python3-setuptools \
"

# running build_ext a second time during install fails, because Python
# would then attempt to import cythonized modules built for the target
# architecture.
DISTUTILS_INSTALL_ARGS += "--skip-build"

do_install_append() {
    # rename scripts that would conflict with the Python 2 build of Cython
    mv ${D}${bindir}/cython ${D}${bindir}/cython3
    mv ${D}${bindir}/cythonize ${D}${bindir}/cythonize3
    mv ${D}${bindir}/cygdb ${D}${bindir}/cygdb3
}
