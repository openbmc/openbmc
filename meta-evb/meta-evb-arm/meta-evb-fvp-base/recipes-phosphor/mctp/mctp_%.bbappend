FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
  file://mctp-local.service \
"

SYSTEMD_SERVICE:${PN} += "mctp-local.service"

do_install:append() {
    install -m 0644 ${UNPACKDIR}/mctp-local.service ${D}${systemd_system_unitdir}/
}

