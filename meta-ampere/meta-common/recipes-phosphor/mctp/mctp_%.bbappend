FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

RDEPENDS:${PN} += "bash"

SRC_URI:append = " \
                  file://mctp-local.service \
                  file://mctpd.conf \
                 "

SYSTEMD_SERVICE:${PN} += "mctp-local.service"

EXTRA_OEMESON:append = " \
                        -Dunsafe-writable-connectivity=true \
                       "

do_install:append() {
    install -m 0644 ${UNPACKDIR}/mctp-local.service ${D}${systemd_system_unitdir}/
    install -d ${D}/etc/
    install -m 0644 ${UNPACKDIR}/mctpd.conf ${D}/etc/mctpd.conf
}

