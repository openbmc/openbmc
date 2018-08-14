SUMMARY = "Pyzmq provides Zero message queue access for the Python language"
HOMEPAGE = "http://zeromq.org/bindings:python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING.BSD;md5=11c65680f637c3df7f58bbc8d133e96e"
DEPENDS = "zeromq"

SRC_URI = "file://club-rpath-out.patch"
SRC_URI[md5sum] = "87e3abb33af5794db5ae85c667bbf324"
SRC_URI[sha256sum] = "77994f80360488e7153e64e5959dc5471531d1648e3a4bff14a714d074a38cc2"

inherit pypi setuptools pkgconfig

RDEPENDS_${PN} += "python-multiprocessing"

FILES_${PN}-dbg =+ "${PYTHON_SITEPACKAGES_DIR}/zmq/backend/cython/.debug"

do_compile_prepend() {
    echo [global] > ${S}/setup.cfg
    echo zmq_prefix = ${STAGING_DIR_HOST} >> ${S}/setup.cfg
    echo have_sys_un_h = True >> ${S}/setup.cfg
    echo skip_check_zmq = True >> ${S}/setup.cfg
    echo libzmq_extension = False >> ${S}/setup.cfg
    echo no_libzmq_extension = True >> ${S}/setup.cfg
}
