SUMMARY = "The Cython language"
HOMEPAGE = "https://pypi.org/project/Cython/"
DESCRIPTION = "Cython is a language specially designed for writing Python extension modules. \
It's designed to bridge the gap between the nice, high-level, easy-to-use world of Python \
and the messy, low-level world of C."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=61c3ee8961575861fa86c7e62bc9f69c"
PYPI_PACKAGE = "Cython"

SRC_URI[sha256sum] = "dcc96739331fb854dcf503f94607576cfe8488066c61ca50dfd55836f132de99"
UPSTREAM_CHECK_REGEX = "Cython-(?P<pver>.*)\.tar"

inherit pypi setuptools3

# running build_ext a second time during install fails, because Python
# would then attempt to import cythonized modules built for the target
# architecture.
SETUPTOOLS_INSTALL_ARGS += "--skip-build"

do_install:append() {
	# Make sure we use /usr/bin/env python3
	for PYTHSCRIPT in `grep -rIl '^#!.*python' ${D}`; do
		sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
	done

    # remove build paths from generated sources
    sed -i -e 's|${WORKDIR}||' ${S}/Cython/*.c ${S}/Cython/Compiler/*.c ${S}/Cython/Plex/*.c

    # rename scripts that would conflict with the Python 2 build of Cython
    mv ${D}${bindir}/cython ${D}${bindir}/cython3
    mv ${D}${bindir}/cythonize ${D}${bindir}/cythonize3
    mv ${D}${bindir}/cygdb ${D}${bindir}/cygdb3
}

PACKAGESPLITFUNCS =+ "cython_fix_sources"

cython_fix_sources () {
	for f in ${PKGD}${TARGET_DBGSRC_DIR}/Cython/Compiler/FlowControl.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Compiler/FusedNode.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Compiler/Scanning.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Compiler/Visitor.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Plex/Actions.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Plex/Scanners.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Runtime/refnanny.c \
		${PKGD}${TARGET_DBGSRC_DIR}/Cython/Tempita/_tempita.c \
		${PKGD}${libdir}/${PYTHON_DIR}/site-packages/Cython*/SOURCES.txt; do
		if [ -e $f ]; then
			sed -i -e 's#${WORKDIR}/Cython-${PV}#${TARGET_DBGSRC_DIR}#g' $f
		fi
	done
}

RDEPENDS:${PN}:class-target += "\
    python3-misc \
    python3-netserver \
    python3-pkgutil \
    python3-pyparsing \
    python3-setuptools \
    python3-shell \
    python3-xml \
"

RDEPENDS:${PN}:class-nativesdk += "\
    nativesdk-python3-misc \
    nativesdk-python3-netserver \
    nativesdk-python3-pkgutil \
    nativesdk-python3-pyparsing \
    nativesdk-python3-setuptools \
    nativesdk-python3-shell \
    nativesdk-python3-xml \
"

BBCLASSEXTEND = "native nativesdk"
