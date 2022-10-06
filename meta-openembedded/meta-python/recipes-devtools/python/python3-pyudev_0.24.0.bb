SUMMARY = "A libudev binding"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[sha256sum] = "b2a3afe1c99ea751f8296652557eac559874da2a1b1ec0625178706ec5a345f3"

inherit pypi setuptools3

do_configure:prepend() {
    sed -i "/import pyudev/d" ${S}/setup.py
    sed -i "s/str(pyudev.__version__)/'${PV}'/g" ${S}/setup.py
}

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-fcntl \
    libudev \
"

BBCLASSEXTEND = "native nativesdk"
