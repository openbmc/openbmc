inherit obmc-phosphor-systemd
SUMMARY = "Power Supplies monitoring for HPE RL300 Gen11"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "psu2-monitoring.service psu1-monitoring.service psu2up.service psu2down.service psu1down.service psu1up.service"
SRC_URI:append = " file://psu2-monitoring.service file://psu1-monitoring.service file://psus-manager.sh file://psu2up.service file://psu2down.service file://psu1down.service file://psu1up.service file://psu2.json file://psu1.json file://startMonitoring1.sh file://startMonitoring2.sh"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
IMAGE_INSTALL += "power-supplies-monitoring "

FILES:${PN} = "/usr/share/gpios/* /usr/bin/* /etc/system/*"

do_install:append() {
  install -d ${D}${sysconfdir}/system/
  install -d ${D}/usr/share/gpios
  install -d ${D}/usr/bin
  install -m 0644 ${UNPACKDIR}/psu1.json ${D}/usr/share/gpios/psu1.json
  install -m 0644 ${UNPACKDIR}/psu2.json ${D}/usr/share/gpios/psu2.json
  install -m 0644 ${UNPACKDIR}/psu2-monitoring.service ${D}${sysconfdir}/system/psu2-monitoring.service
  install -m 0644 ${UNPACKDIR}/psu1-monitoring.service ${D}${sysconfdir}/system/psu1-monitoring.service
  install -m 0644 ${UNPACKDIR}/psu2up.service ${D}${sysconfdir}/system/psu2up.service
  install -m 0644 ${UNPACKDIR}/psu1up.service ${D}${sysconfdir}/system/psu1up.service
  install -m 0644 ${UNPACKDIR}/psu1down.service ${D}${sysconfdir}/system/psu1down.service
  install -m 0644 ${UNPACKDIR}/psu1down.service ${D}${sysconfdir}/system/psu1down.service
  install -m 0755 ${UNPACKDIR}/psus-manager.sh ${D}/usr/bin/psus-manager.sh
  install -m 0755 ${UNPACKDIR}/startMonitoring1.sh ${D}/usr/bin/startMonitoring1.sh
  install -m 0755 ${UNPACKDIR}/startMonitoring2.sh ${D}/usr/bin/startMonitoring2.sh
}
