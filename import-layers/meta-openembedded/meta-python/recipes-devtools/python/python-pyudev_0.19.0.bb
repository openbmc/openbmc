SUMMARY = "A libudev binding"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI[md5sum] = "1151e9d05baf6ce7b43e7574dc0ef154"
SRC_URI[sha256sum] = "5abcbd03e4965110d1fedcbdd5532974cb4638ceef34337aa2d5758eceb54ad3"

inherit pypi setuptools

do_configure_prepend() {
    sed -i "/import pyudev/d" ${S}/setup.py
    sed -i "s/str(pyudev.__version__)/'${PV}'/g" ${S}/setup.py
}

RDEPENDS_${PN} = "\
    python-ctypes \
    python-subprocess \
    python-misc \
    python-contextlib \
"
