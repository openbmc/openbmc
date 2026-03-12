SUMMARY = "A libudev binding"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[sha256sum] = "e788bb983700b1a84efc2e88862b0a51af2a995d5b86bc9997546505cf7b36bc"

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
