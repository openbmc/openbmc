SUMMARY = "A libudev binding"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[sha256sum] = "32ae3585b320a51bc283e0a04000fd8a25599edb44541e2f5034f6afee5d15cc"

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
