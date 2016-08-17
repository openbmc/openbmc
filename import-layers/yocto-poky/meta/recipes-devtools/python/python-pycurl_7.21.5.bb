SUMMARY = "Python bindings for libcurl"
HOMEPAGE = "http://pycurl.sourceforge.net/"
SECTION = "devel/python"
LICENSE = "LGPLv2.1+ | MIT"
LIC_FILES_CHKSUM = "file://README.rst;beginline=166;endline=182;md5=a84a1caa65b89d4584b693d3680062fb \
                    file://COPYING-LGPL;md5=3579a9fd0221d49a237aaa33492f988c \
                    file://COPYING-MIT;md5=b7e434aeb228ed731c00bcf177e79b19"

DEPENDS = "curl python"
RDEPENDS_${PN} = "python-core curl"
SRCNAME = "pycurl"

SRC_URI = "\
  http://${SRCNAME}.sourceforge.net/download/${SRCNAME}-${PV}.tar.gz;name=archive \
  file://no-static-link.patch \
"

SRC_URI[archive.md5sum] = "bca7bf47320082588db544ced2ba8717"
SRC_URI[archive.sha256sum] = "8a1e0eb55573388275a1d6c2534ca4cfca5d7fa772b99b505c08fa149b27aed0"
S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit distutils

BBCLASSEXTEND = "native"

# Ensure the docstrings are generated as make clean will remove them
do_compile_prepend() {
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py docstrings
}

do_install_append() {
	rm -rf ${D}${datadir}/share
}
