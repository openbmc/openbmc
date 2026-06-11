SUMMARY = "QEMU Monitor Protocol Python library"
DESCRIPTION = "An asyncio library for communicating with QEMU Monitor Protocol (QMP). \
This library was split out of the QEMU source tree to provide a reference QMP \
implementation usable both within and outside of the QEMU source tree."
HOMEPAGE = "https://gitlab.com/qemu-project/python-qemu-qmp"
LICENSE = "LGPL-2.0-only & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4cf66a4984120007c9881cc871cf49db"

inherit pypi python_setuptools_build_meta

PYPI_PACKAGE = "qemu_qmp"

SRC_URI[sha256sum] = "a3c25d871fab549122b2340810de1f99481002c942a2132476b062aacdbf6e92"

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "python3-core python3-asyncio"

# Install to the custom path expected by oeqa/utils/qemurunner.py
# which imports "qmp.legacy" from ${libdir}/qemu-python/
do_install:append:class-native() {
    install -d ${D}${libdir}/qemu-python/qmp/
    cp -R --no-dereference --preserve=mode,links ${S}/qemu/qmp/* ${D}${libdir}/qemu-python/qmp/
}

FILES:${PN}:append:class-native = " ${libdir}/qemu-python"

BBCLASSEXTEND = "native nativesdk"
