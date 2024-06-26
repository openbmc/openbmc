FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
        file://mctp \
        file://service-override.conf \
        "

do_install:append() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${UNPACKDIR}/mctp ${D}${sysconfdir}/default/mctp
}
