SUMMARY = "GPIO based powercontrol for a host system"
DESCRIPTION = "GPIO based powercontrol for a host system."
PR = "r1"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit systemd

RDEPENDS:${PN} += " \
  bash \
  gpio-ctrl \
  "

SRC_URI += " \
  file://host-ensure-off.service \
  file://host-powercycle-watchdog.service \
  file://host-powercycle.service \
  file://host-poweroff-watchdog.service \
  file://host-poweroff.service \
  file://host-poweron.service \
  file://host-reset-cold-watchdog.service \
  file://host-reset-cold.service \
  file://host-reset-warm-watchdog.service \
  file://host-reset-warm.service \
  file://host_ensure_off.sh \
  file://host_isoff.sh \
  file://host_powercycle.sh \
  file://host_poweroff.sh \
  file://host_poweron.sh \
  file://host_reset.sh \
  file://lib.sh \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} += " \
  host-reset-cold.service \
  host-reset-cold-watchdog.service \
  host-reset-warm.service \
  host-reset-warm-watchdog.service \
  host-ensure-off.service \
  host-powercycle.service \
  host-powercycle-watchdog.service \
  host-poweroff.service \
  host-poweroff-watchdog.service \
  host-poweron.service \
  "

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/host_*.sh ${D}${bindir}/

    install -d ${D}${datadir}/gpio-host-pwr
    install -m 0755 ${UNPACKDIR}/lib.sh ${D}${datadir}/gpio-host-pwr/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/*.service ${D}${systemd_system_unitdir}/
}
