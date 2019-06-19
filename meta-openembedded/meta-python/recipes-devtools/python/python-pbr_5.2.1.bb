inherit setuptools
# The inc file is in oe-core
require recipes-devtools/python/python-pbr.inc

SRC_URI[md5sum] = "f72c2dd10602abad3695097d634e94bb"
SRC_URI[sha256sum] = "93d2dc6ee0c9af4dbc70bc1251d0e545a9910ca8863774761f92716dece400b6"

do_install_append() {
        if [ -f ${D}${bindir}/pbr ]; then
                mv ${D}${bindir}/pbr ${D}${bindir}/pbr-2
        fi
}

