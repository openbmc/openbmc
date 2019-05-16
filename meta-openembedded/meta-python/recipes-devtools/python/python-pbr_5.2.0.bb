inherit setuptools
# The inc file is in oe-core
require recipes-devtools/python/python-pbr.inc

SRC_URI[md5sum] = "2bca008fd08d035a2f78c606d876a6db"
SRC_URI[sha256sum] = "d950c64aeea5456bbd147468382a5bb77fe692c13c9f00f0219814ce5b642755"

do_install_append() {
        if [ -f ${D}${bindir}/pbr ]; then
                mv ${D}${bindir}/pbr ${D}${bindir}/pbr-2
        fi
}

