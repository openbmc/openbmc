FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
  file://40-system.conf \
  "

FILES:${PN}:append = " \
  ${systemd_unitdir}/system.conf.d/40-system.conf \
  "

do_install:append() {
    install -d -m 0755 ${D}${systemd_unitdir}/system.conf.d/
    install -m 0644 ${WORKDIR}/40-system.conf ${D}${systemd_unitdir}/system.conf.d/
}
