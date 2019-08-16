inherit setuptools
# The inc file is in oe-core
require recipes-devtools/python/python-pbr.inc

SRC_URI[md5sum] = "ea90e1118a0132da752d45e68d10b2b8"
SRC_URI[sha256sum] = "9b321c204a88d8ab5082699469f52cc94c5da45c51f114113d01b3d993c24cdf"

do_install_append() {
        if [ -f ${D}${bindir}/pbr ]; then
                mv ${D}${bindir}/pbr ${D}${bindir}/pbr-2
        fi
}

