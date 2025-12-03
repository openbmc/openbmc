FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

install_mctp_configuration() {
      install -d ${D}${sysconfdir}/default
      install -m 0644 ${UNPACKDIR}/mctp ${D}${sysconfdir}/default/mctp
      install -d ${D}${systemd_system_unitdir}/mctp-demux.service.d
      install -D -m 0644 ${UNPACKDIR}/service-override.conf ${D}${systemd_system_unitdir}/mctp-demux.service.d/
}

SRC_URI:append:ibm-enterprise = " file://mctp"
SRC_URI:append:ibm-enterprise = " file://service-override.conf"
do_install:append:ibm-enterprise() {
      install_mctp_configuration
}

FILES:${PN}:append = " ${systemd_system_unitdir}/mctp-demux.service.d/service-override.conf"
