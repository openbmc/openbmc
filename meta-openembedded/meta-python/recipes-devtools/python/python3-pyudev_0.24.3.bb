SUMMARY = "A libudev binding"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[sha256sum] = "2e945427a21674893bb97632401db62139d91cea1ee96137cc7b07ad22198fc7"

inherit pypi python_setuptools_build_meta

do_configure:prepend() {
    sed -i "/import pyudev/d" ${S}/setup.py
    sed -i "s/str(pyudev.__version__)/'${PV}'/g" ${S}/setup.py
}

RDEPENDS:${PN} = "\
    python3-ctypes \
    python3-misc \
    python3-six \
    python3-threading \
    python3-fcntl \
    libudev \
"
