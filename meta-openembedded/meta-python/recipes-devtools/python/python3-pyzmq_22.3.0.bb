SUMMARY = "Pyzmq provides Zero message queue access for the Python language"
HOMEPAGE = "http://zeromq.org/bindings:python"
LICENSE = "BSD & LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=11c65680f637c3df7f58bbc8d133e96e \
                    file://COPYING.LESSER;md5=12c592fa0bcfff3fb0977b066e9cb69e"
DEPENDS = "zeromq"

FILESEXTRAPATHS:prepend := "${THISDIR}/python-pyzmq:"

SRC_URI += "file://club-rpath-out.patch"
SRC_URI[sha256sum] = "8eddc033e716f8c91c6a2112f0a8ebc5e00532b4a6ae1eb0ccc48e027f9c671c"

inherit pypi pkgconfig setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-multiprocessing \
	${PYTHON_PN}-json \
"

FILES:${PN}-dbg =+ "${PYTHON_SITEPACKAGES_DIR}/zmq/backend/cython/.debug"

do_compile:prepend() {
    echo [global] > ${S}/setup.cfg
    echo zmq_prefix = ${STAGING_DIR_HOST} >> ${S}/setup.cfg
    echo have_sys_un_h = True >> ${S}/setup.cfg
    echo skip_check_zmq = True >> ${S}/setup.cfg
    echo libzmq_extension = False >> ${S}/setup.cfg
    echo no_libzmq_extension = True >> ${S}/setup.cfg
}
