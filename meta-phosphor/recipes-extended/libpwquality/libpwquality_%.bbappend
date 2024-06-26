EXTRA_OECONF:append = " --enable-python-bindings=no"
RDEPENDS:${PN}:remove:class-target = " ${PYTHON_PN}-core"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
        file://pwquality.conf \
        "

do_install:append() {
    install -d ${D}/etc/security
    install -m 0644 ${UNPACKDIR}/pwquality.conf ${D}/etc/security
}
