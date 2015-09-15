SUMMARY = "Python bindings for libcurl"
HOMEPAGE = "http://pycurl.sourceforge.net/"
SECTION = "devel/python"
LICENSE = "LGPLv2.1+ | MIT"
LIC_FILES_CHKSUM = "file://README.rst;beginline=166;endline=181;md5=57e5ab0c0f964533fc59d93dec5695bb \
                    file://COPYING-LGPL;md5=3579a9fd0221d49a237aaa33492f988c \
                    file://COPYING-MIT;md5=e8200955c773b2a0fd6cea36ea5e87be"

DEPENDS = "curl python"
RDEPENDS_${PN} = "python-core curl"
SRCNAME = "pycurl"

SRC_URI = "\
  http://${SRCNAME}.sourceforge.net/download/${SRCNAME}-${PV}.tar.gz;name=archive \
  file://no-static-link.patch \
"

SRC_URI[archive.md5sum] = "f44cd54256d7a643ab7b16e3f409b26b"
SRC_URI[archive.sha256sum] = "6e9770f80459757f73bd71af82fbb29cd398b38388cdf1beab31ea91a331bc6c"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

# need to export these variables for python-config to work
export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

BBCLASSEXTEND = "native"

# Ensure the docstrings are generated as make clean will remove them
do_compile_prepend() {
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py docstrings
}

do_install_append() {
	rm -rf ${D}${datadir}/share
}
