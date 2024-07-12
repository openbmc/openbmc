FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

require conf/recipes/fb-consoles.inc

# Disable obmc-console ssh ports.
PACKAGECONFIG:remove = "ssh"
# Remove default config.
SRC_URI:remove = "file://${BPN}.conf"

OBMC_BMC_TTY = "ttyS4"
SERVER_CONFS = "${@ ' '.join([ f'file://server.{i}.conf' for i in d.getVar('OBMC_CONSOLE_TTYS', True).split() ])}"

SRC_URI:append = " ${SERVER_CONFS}"
SRC_URI:append = " file://client.conf "

do_install:append() {
  install -m 0644 ${WORKDIR}/client.conf ${D}${sysconfdir}/${BPN}/client.conf
}
