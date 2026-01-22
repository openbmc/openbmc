FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://80-mctp-usb.link \
    file://setup-mctp-intf \
    file://setup-mctp-intf.conf \
    "

RDEPENDS:${PN} += " bash"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/mctpd.service.d/setup-mctp-intf.conf \
    ${systemd_unitdir}/network/80-mctp-usb.link \
    "

do_install:append() {
    override_dir=${D}${systemd_system_unitdir}/mctpd.service.d

    install -d ${D}${systemd_system_unitdir}/mctpd.service.d
    install -m 0644 ${UNPACKDIR}/setup-mctp-intf.conf ${override_dir}/setup-mctp-intf.conf

    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/setup-mctp-intf ${D}${libexecdir}/mctp/

    install -d 0755 ${D}${systemd_unitdir}/network
    install -m 0644 ${UNPACKDIR}/80-mctp-usb.link ${D}${systemd_unitdir}/network/
}