# Should RDEPENDS on python at the very least.
FILESEXTRAPATHS_append := "${THISDIR}/files:"

inherit systemd
inherit obmc-phosphor-license

S = "${WORKDIR}/"

SRC_URI = "file://init_once.sh \
	file://poweroff.sh \
	file://poweron.sh \
	file://host-gpio.service \
	file://host-poweroff.service \
	file://host-poweron.service"

DEPENDS = "systemd"
RDEPENDS_${PN} = "bash"

FILES_${PN} += "${sbindir}/ ${systemd_unitdir}/system/"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "host-gpio.service host-poweron.service host-poweroff.service"

# didn't use sbindir because specified usr/sbin in service.
do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${S}init_once.sh ${D}/usr/sbin/.
    install -m 0755 ${S}poweroff.sh ${D}/usr/sbin/.
    install -m 0755 ${S}poweron.sh ${D}/usr/sbin/.

    install -d ${D}${systemd_unitdir}/system

    # Required for systemd_populate_packages
    install -m444 ${S}/host-gpio.service ${D}${systemd_unitdir}/system/host-gpio.service
    install -m444 ${S}/host-poweron.service ${D}${systemd_unitdir}/system/host-poweron.service
    install -m444 ${S}/host-poweroff.service ${D}${systemd_unitdir}/system/host-poweroff.service
}
