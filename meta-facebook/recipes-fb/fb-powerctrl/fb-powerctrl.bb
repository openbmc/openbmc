FILESEXTRAPATHS_append := "${THISDIR}/files:"

inherit obmc-phosphor-systemd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8328fd2a610bf4527feedcaa3ae3d14"

S = "${WORKDIR}"

SRC_URI = "file://setup_gpio.sh \
           file://power-util \
           file://host-gpio.service \
           file://host-poweroff.service \
           file://host-poweron.service \
           file://LICENSE"

DEPENDS = "systemd"
RDEPENDS_${PN} = "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "host-gpio.service host-poweron.service host-poweroff.service"

do_install() {
    install -d ${D}/usr/sbin
    install -m 0755 ${S}/setup_gpio.sh ${D}/${sbindir}/
    install -m 0755 ${S}/power-util ${D}/${sbindir}/
}
