DESCRIPTION = "Cython is a language specially designed for writing Python extension modules. \
It's designed to bridge the gap between the nice, high-level, easy-to-use world of Python \
and the messy, low-level world of C."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"
PYPI_PACKAGE = "Cython"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "b93750b34382b025d678bea819b7491b"
SRC_URI[sha256sum] = "e6840a2ba2704f4ffb40e454c36f73aeb440a4005453ee8d7ff6a00d812ba176"

inherit pypi

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-shell \
"

do_install_append() {
	# Make sure we use /usr/bin/env python
	for PYTHSCRIPT in `grep -rIl '^#!.*python' ${D}`; do
		sed -i -e '1s|^#!.*|#!/usr/bin/env ${PYTHON_PN}|' $PYTHSCRIPT
	done
}
