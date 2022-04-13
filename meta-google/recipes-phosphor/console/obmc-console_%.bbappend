FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " \
  file://serial-to-bmc@.service \
  file://serial-to-host@.service.in \
  file://host-console-journal.service.in \
  file://host-console-filtered.sh \
  "

SYSTEMD_SERVICE:${PN}:append:gbmc = " serial-to-bmc@.service"
SYSTEMD_SERVICE:${PN}:append:gbmc= " serial-to-host@.service"

OBMC_CONSOLE_FRONT_TTY ?= ""

RDEPENDS:${PN}:append:gbmc = " bash"

FILES:${PN}:append:gbmc = " ${systemd_system_unitdir}/host-console-journal.service"

do_install:append:gbmc() {
  install -d ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/serial-to-bmc@.service \
    ${D}${systemd_system_unitdir}

  sed "s,@HOST_TTY@,${OBMC_CONSOLE_HOST_TTY}," \
    ${WORKDIR}/serial-to-host@.service.in \
    >${D}${systemd_system_unitdir}/serial-to-host@.service

  sed 's,@HOST_TTY@,${OBMC_CONSOLE_HOST_TTY},' \
    ${WORKDIR}/host-console-journal.service.in \
    >${D}${systemd_system_unitdir}/host-console-journal.service

  install -d ${D}${bindir}
  install -m0755 ${WORKDIR}/host-console-filtered.sh ${D}${bindir}/
}

pkg_postinst:${PN}:append:gbmc () {
  if [ -n "${OBMC_CONSOLE_FRONT_TTY}" ]; then
    systemctl --root=$D enable serial-to-host@${OBMC_CONSOLE_FRONT_TTY}.service
  fi
}

pkg_postinst:${PN}:append:gbmc:dev () {
  systemctl --root=$D enable host-console-journal.service
}
