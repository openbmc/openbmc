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

PACKAGEBUILDPKGD += "cython_fix_sources"

cython_fix_sources () {
	sed -i -e 's#${WORKDIR}#/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}#g' \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Compiler/FlowControl.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Compiler/FusedNode.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Compiler/Scanning.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Compiler/Visitor.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Plex/Actions.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Plex/Scanners.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Runtime/refnanny.c \
		${PKGD}/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/Cython-${PV}/Cython/Tempita/_tempita.c \
		${PKGD}${libdir}/${PYTHON_DIR}/site-packages/Cython*/SOURCES.txt
}
