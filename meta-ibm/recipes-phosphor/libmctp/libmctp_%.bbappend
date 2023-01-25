FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

install_mctp_configuration() {
      install -d ${D}${sysconfdir}/default
      install -m 0644 ${WORKDIR}/mctp ${D}${sysconfdir}/default/mctp
}

SRC_URI:append:p10bmc = " file://mctp"
do_install:append:p10bmc() {
      install_mctp_configuration
}

SRC_URI:append:witherspoon-tacoma = " file://mctp"
do_install:append:witherspoon-tacoma() {
      install_mctp_configuration
}
