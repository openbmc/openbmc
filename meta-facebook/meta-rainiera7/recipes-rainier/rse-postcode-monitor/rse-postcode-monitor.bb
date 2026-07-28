SUMMARY = "RSE GPIO postcode monitor (Bash daemon)"
LICENSE = "Apache-2.0"
APACHE_2_MD5 = "89aea4e17d99a7cacdbeed46a0096b10"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=${APACHE_2_MD5}"

inherit systemd

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = " \
    file://rse-postcode-monitor.sh \
    file://rse-postcode-monitor@.service \
    file://rse-postcode-monitor-c0.env \
    file://rse-postcode-monitor-c1.env \
"

S = "${UNPACKDIR}"

RDEPENDS:${PN}:append = " \
    bash \
    libgpiod-tools \
    systemd \
"

SYSTEMD_SERVICE:${PN} = " \
    rse-postcode-monitor@c0.service \
    rse-postcode-monitor@c1.service \
"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN}:append = " \
    ${systemd_system_unitdir}/rse-postcode-monitor@.service \
"

do_install() {
    install -d ${D}${libexecdir}/rse-postcode-monitor
    install -m 0755 ${S}/rse-postcode-monitor.sh \
        ${D}${libexecdir}/rse-postcode-monitor/rse-postcode-monitor.sh

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/rse-postcode-monitor@.service \
        ${D}${systemd_system_unitdir}/rse-postcode-monitor@.service

    install -d ${D}${sysconfdir}/default
    install -m 0644 ${S}/rse-postcode-monitor-c0.env \
        ${D}${sysconfdir}/default/rse-postcode-monitor-c0.env
    install -m 0644 ${S}/rse-postcode-monitor-c1.env \
        ${D}${sysconfdir}/default/rse-postcode-monitor-c1.env
}