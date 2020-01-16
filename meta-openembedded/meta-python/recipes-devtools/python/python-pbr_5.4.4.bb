inherit setuptools
# The inc file is in oe-core
require recipes-devtools/python/python-pbr.inc

SRC_URI[md5sum] = "65cdc32e1a1ff56d481fc15aa8caf988"
SRC_URI[sha256sum] = "139d2625547dbfa5fb0b81daebb39601c478c21956dc57e2e07b74450a8c506b"

do_install_append() {
        if [ -f ${D}${bindir}/pbr ]; then
                mv ${D}${bindir}/pbr ${D}${bindir}/pbr-2
        fi
}

