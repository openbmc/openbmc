FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/files:"
SRC_URI:append:gbmc = " file://avahi-daemon.conf"
do_install:append:gbmc() {
  install -m0644 ${WORKDIR}/avahi-daemon.conf ${D}${sysconfdir}/avahi/
}
