SUMMARY = "Configures systemd settings for gBMC"
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

S = "${WORKDIR}"

SRC_URI:append = " \
  file://firmware-updates.target \
  file://firmware-updates-pre.target \
  file://40-gbmc-forward.conf \
  file://40-system.conf \
  file://40-gbmc-time.conf \
  "

FILES:${PN}:append = " \
  ${systemd_unitdir}/coredump.conf.d/40-gbmc-coredump.conf \
  ${systemd_unitdir}/resolved.conf.d/40-gbmc-nomdns.conf \
  ${systemd_unitdir}/system.conf.d/40-system.conf \
  ${systemd_unitdir}/timesyncd.conf.d/40-gbmc-time.conf \
  ${libdir}/sysctl.d/40-gbmc-forward.conf \
  "

FILES:${PN}:append:dev = " \
  ${libdir}/sysctl.d/40-gbmc-debug.conf \
  "

SYSTEMD_SERVICE:${PN}:append = " \
  firmware-updates.target \
  firmware-updates-pre.target \
  "

# Put coredumps in the journal to ensure they stay in ram
do_install() {
  install -d -m 0755 ${D}${systemd_unitdir}/coredump.conf.d
  printf "[Coredump]\nStorage=journal\n" \
    >${D}${systemd_unitdir}/coredump.conf.d/40-gbmc-coredump.conf

  install -d -m 0755 ${D}${systemd_unitdir}/resolved.conf.d
  printf "[Resolve]\nLLMNR=no\nMulticastDNS=resolve\n" \
    >${D}${systemd_unitdir}/resolved.conf.d/40-gbmc-nomdns.conf

  install -d -m 0755 ${D}${systemd_system_unitdir}
  install -m 0644 ${WORKDIR}/firmware-updates.target ${D}${systemd_system_unitdir}/
  install -m 0644 ${WORKDIR}/firmware-updates-pre.target ${D}${systemd_system_unitdir}/

  install -d -m0755 ${D}${libdir}/sysctl.d
  install -m 0644 ${WORKDIR}/40-gbmc-forward.conf ${D}${libdir}/sysctl.d/

  install -d -m 0755 ${D}${systemd_unitdir}/system.conf.d/
  install -D -m0644 ${WORKDIR}/40-system.conf ${D}${systemd_unitdir}/system.conf.d/

  install -d -m 0755 ${D}${systemd_unitdir}/timesyncd.conf.d/
  install -D -m0644 ${WORKDIR}/40-gbmc-time.conf ${D}${systemd_unitdir}/timesyncd.conf.d/
}

do_install:append:dev() {
  printf "kernel.sysrq = 1\n" \
      >${D}${libdir}/sysctl.d/40-gbmc-debug.conf
}
