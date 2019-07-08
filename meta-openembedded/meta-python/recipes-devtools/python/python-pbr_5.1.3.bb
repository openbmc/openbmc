inherit setuptools
# The inc file is in oe-core
require recipes-devtools/python/python-pbr.inc

SRC_URI[md5sum] = "08972dca5fd2a959f27842090973edd6"
SRC_URI[sha256sum] = "8c361cc353d988e4f5b998555c88098b9d5964c2e11acf7b0d21925a66bb5824"

do_install_append() {
        if [ -f ${D}${bindir}/pbr ]; then
                mv ${D}${bindir}/pbr ${D}${bindir}/pbr-2
        fi
}

