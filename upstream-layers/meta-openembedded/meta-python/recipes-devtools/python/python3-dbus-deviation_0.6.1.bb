SUMMARY = "dbus-deviation is a project for parsing D-Bus introspection XML and processing it in various ways"
HOMEPAGE = "https://tecnocode.co.uk/dbus-deviation/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=90263a49bc1d9a204656fec4d5616c66"

SRC_URI[sha256sum] = "e06b88efe223885d2725df51cf7c9b7b463d1c6f04ea49d4690874318d0eb7a3"

inherit pypi setuptools3

SRC_URI += "file://0001-Prevent-trying-to-donwload-requierment-which-will-ca.patch"

DEPENDS += "python3-sphinx-native"

do_install:append() {
    for ss in $(find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.py"); do
        sed -i 's,/usr/bin/python$,/usr/bin/env python3,' "$ss"
    done
}

BBCLASSEXTEND = "native"
