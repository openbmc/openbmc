SUMMARY = "The Cython language"
HOMEPAGE = "https://pypi.org/project/Cython/"
DESCRIPTION = "Cython is a language specially designed for writing Python extension modules. \
It's designed to bridge the gap between the nice, high-level, easy-to-use world of Python \
and the messy, low-level world of C."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=61c3ee8961575861fa86c7e62bc9f69c"
PYPI_PACKAGE = "Cython"
BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "a2d354f059d1f055d34cfaa62c5b68bc78ac2ceab6407148d47fb508cf3ba4f3"
UPSTREAM_CHECK_REGEX = "Cython-(?P<pver>.*)\.tar"

inherit pypi

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

do_install:append() {
	# Make sure we use /usr/bin/env python
	for PYTHSCRIPT in `grep -rIl '^#!.*python' ${D}`; do
		sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
	done

        # remove build paths from generated sources
        sed -i -e 's|${WORKDIR}||' ${S}/Cython/*.c ${S}/Cython/Compiler/*.c ${S}/Cython/Plex/*.c
}
