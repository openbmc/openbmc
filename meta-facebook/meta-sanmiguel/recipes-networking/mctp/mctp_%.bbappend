FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit obmc-phosphor-systemd systemd

SRC_URI:append = " \
    file://80-mctp-usb.link \
    file://99-sma-mctp-eid.rules \
    file://sma-mctp-eid-config \
    "

RDEPENDS:${PN} += " bash"

FILES:${PN}:append = " \
    ${systemd_unitdir}/network/80-mctp-usb.link \
    "

SYSTEMD_SERVICE:${PN}:append = " \
    sma-mctp-eid-config@.service \
    "

SMA_MCTP_INSTANCES = " \
    mcu1u2u1u1 \
    mcu1u2u2u1 \
    mcu1u2u1u4u1 \
    mcu1u2u1u3u1 \
    mcu1u2u2u3u1 \
    mcu1u2u2u4u1 \
    mcu1u1u4u1 \
"

SMA_MCTP_EID_INSTFMT = "sma-mctp-eid-config@.service:sma-mctp-eid-config@{0}.service"
SYSTEMD_LINK:${PN} += "${@compose_list_zip(d, 'SMA_MCTP_EID_INSTFMT', 'SMA_MCTP_INSTANCES')}"

do_install:append() {
    install -d ${D}${libexecdir}/mctp
    install -m 0755 ${UNPACKDIR}/sma-mctp-eid-config ${D}${libexecdir}/mctp/

    install -d 0755 ${D}${systemd_unitdir}/network
    install -m 0644 ${UNPACKDIR}/80-mctp-usb.link ${D}${systemd_unitdir}/network/

    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${UNPACKDIR}/99-sma-mctp-eid.rules ${D}${sysconfdir}/udev/rules.d/
}