inherit obmc-phosphor-systemd
SUMMARY = "Power Sequencing for HPE RL300 Gen11"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "monitor-ipmi-power-transition.service power-sequencing.service gpiosdown.service gpiosup.service powerbuttonpressed.service powerbuttonreleased.service"
SRC_URI:append = " file://monitor-ipmi-power-transition.service file://ipmi-power-button-monitoring.sh file://rl300Start.sh file://power-sequencing.service file://gpiosdown.service file://gpiosup.service file://gpios-manager.sh file://test.json file://powerbuttonreleased.service file://powerbuttonpressed.service file://power-button.sh file://startMonitoring.sh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
IMAGE_INSTALL += "power-sequencing "

FILES:${PN} = "/usr/share/gpios/* /usr/bin/* /etc/system/*"

do_install:append() {
  install -d ${D}${sysconfdir}/system/
  install -d ${D}/usr/share/gpios
  install -d ${D}/usr/bin
  install -m 0644 ${UNPACKDIR}/test.json ${D}/usr/share/gpios/test.json
  install -m 0644 ${UNPACKDIR}/power-sequencing.service ${D}${sysconfdir}/system/power-sequencing.service
  install -m 0644 ${UNPACKDIR}/gpiosup.service ${D}${sysconfdir}/system/gpiosup.service
  install -m 0644 ${UNPACKDIR}/gpiosdown.service ${D}${sysconfdir}/system/gpiosdown.service
  install -m 0644 ${UNPACKDIR}/powerbuttonpressed.service ${D}${sysconfdir}/system/powerbuttonpressed.service
  install -m 0644 ${UNPACKDIR}/powerbuttonreleased.service ${D}${sysconfdir}/system/powerbuttonreleased.service
  install -m 0644 ${UNPACKDIR}/monitor-ipmi-power-transition.service ${D}${sysconfdir}/system/monitor-ipmi-power-transition.service
  install -m 0755 ${UNPACKDIR}/gpios-manager.sh ${D}/usr/bin/gpios-manager.sh
  install -m 0755 ${UNPACKDIR}/startMonitoring.sh ${D}/usr/bin/startMonitoring.sh
  install -m 0755 ${UNPACKDIR}/rl300Start.sh ${D}/usr/bin/rl300Start.sh
  install -m 0755 ${UNPACKDIR}/ipmi-power-button-monitoring.sh ${D}/usr/bin/ipmi-power-button-monitoring.sh
  install -m 0755 ${UNPACKDIR}/power-button.sh ${D}/usr/bin/power-button.sh
}
