SUMMARY = "A libudev binding"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[md5sum] = "cf4d9db7d772622144ca1be6b5d9353b"
SRC_URI[sha256sum] = "094b7a100150114748aaa3b70663485dd360457a709bfaaafe5a977371033f2b"

do_configure_prepend() {
    sed -i "/import pyudev/d" ${S}/setup.py
    sed -i "s/str(pyudev.__version__)/'${PV}'/g" ${S}/setup.py
}

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-threading \
"
