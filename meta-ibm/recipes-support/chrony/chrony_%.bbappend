FILESEXTRAPATHS:prepend := "${THISDIR}/chrony:"

install_chrony_configuration() {
    install -D -m 0644 ${UNPACKDIR}/chronyd.service ${D}${systemd_system_unitdir}/chronyd.service
    install -m 644 -D ${UNPACKDIR}/chrony.conf ${D}${sysconfdir}/chrony.conf
}

SRC_URI:append:df-chrony = " file://chronyd.service"
SRC_URI:append:df-chrony = " file://chrony.conf"
do_install:append:huygens() {
      install_chrony_configuration
}
