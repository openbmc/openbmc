FILESEXTRAPATHS:prepend:gbmc := "${THISDIR}/${PN}:"

SRC_URI:append:gbmc = " file://default"

SYSTEMD_SERVICE:${PN}:append:gbmc = " rngd-nojitter.service"

do_install:append:gbmc() {
  install -m 0644 ${D}${systemd_system_unitdir}/rngd.service \
    ${D}${systemd_system_unitdir}/rngd-nojitter.service
  # Don't enable jitter in rngd-nojitter
  sed -i 's,\$EXTRA_ARGS,-x jitter \$EXTRA_ARGS,' \
    ${D}${systemd_system_unitdir}/rngd-nojitter.service
  # Run the jitter enabled service if this one fails
  sed -i '/^\[Unit\]$/aOnFailure=rngd.service\nConflicts=rngd.service' \
    ${D}${systemd_system_unitdir}/rngd-nojitter.service

  # Don't run the jitter service by default
  sed -i '/^WantedBy=/d' ${D}${systemd_system_unitdir}/rngd.service
}
