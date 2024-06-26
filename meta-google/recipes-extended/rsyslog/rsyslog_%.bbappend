FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " \
  file://rsyslog.conf \
  file://journald.conf \
"

PACKAGECONFIG:append:gbmc = " imjournal"

FILES:${PN}:append:gbmc = "\
    ${systemd_unitdir}/journald.conf.d/ \
"

# Set ForwardToSyslog=no
# Disable forward journal to socket since that is not being used.
do_install:append:gbmc() {
  install -D -m0644 ${UNPACKDIR}/journald.conf \
    ${D}${systemd_unitdir}/journald.conf.d/99-${PN}.conf
}
