FILESEXTRAPATHS_prepend_gbmc := "${THISDIR}/files:"
SRC_URI_append_gbmc = " file://avahi-daemon.conf"
do_install_append_gbmc() {
  install -m0644 ${WORKDIR}/avahi-daemon.conf ${D}${sysconfdir}/avahi/
}
