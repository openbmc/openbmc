SUMMARY = "Pyzmq provides Zero message queue access for the Python language"
HOMEPAGE = "http://zeromq.org/bindings:python"
LICENSE = "BSD & LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=11c65680f637c3df7f58bbc8d133e96e \
                    file://COPYING.LESSER;md5=12c592fa0bcfff3fb0977b066e9cb69e"
DEPENDS = "zeromq"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-pyzmq:"

SRC_URI += "file://club-rpath-out.patch"
SRC_URI[md5sum] = "aecdfc328193fbd81f6dc23228319943"
SRC_URI[sha256sum] = "2199f753a230e26aec5238b0518b036780708a4c887d4944519681a920b9dee4"

inherit pypi pkgconfig

RDEPENDS_${PN} += "${PYTHON_PN}-multiprocessing"

FILES_${PN}-dbg =+ "${PYTHON_SITEPACKAGES_DIR}/zmq/backend/cython/.debug"

do_compile_prepend() {
    echo [global] > ${S}/setup.cfg
    echo zmq_prefix = ${STAGING_DIR_HOST} >> ${S}/setup.cfg
    echo have_sys_un_h = True >> ${S}/setup.cfg
    echo skip_check_zmq = True >> ${S}/setup.cfg
    echo libzmq_extension = False >> ${S}/setup.cfg
    echo no_libzmq_extension = True >> ${S}/setup.cfg
}
