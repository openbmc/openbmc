FILESEXTRAPATHS_append_tiogapass := "${THISDIR}/files:"
FILESEXTRAPATHS_append_yosemitev2 := "${THISDIR}/files/yosemitev2:"

inherit obmc-phosphor-systemd
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8328fd2a610bf4527feedcaa3ae3d14"

S = "${WORKDIR}/"

SRC_URI_append_tiogapass = "file://setup_gpio.sh \
           file://power-util \
           file://host-gpio.service \
           file://host-poweroff.service \
           file://host-poweron.service \
           file://LICENSE"

SRC_URI_append_yosemitev2 = "file://setup_gpio.sh \
           file://power-util \
           file://power_led.sh \
           file://ast-functions \
           file://host-gpio.service \
           file://host-poweroff.service \
           file://host-poweron.service \
           file://LICENSE"

DEPENDS = "systemd"
RDEPENDS_${PN} = "bash"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "host-gpio.service host-poweron.service host-poweroff.service"

do_install_append_tiogapass(){
    install -d ${D}/usr/sbin
    install -m 0755 ${S}setup_gpio.sh ${D}/${sbindir}/
    install -m 0755 ${S}power-util ${D}/${sbindir}/
}

do_install_append_yosemitev2(){
    install -d ${D}/usr/sbin
    install -m 0755 ${S}setup_gpio.sh ${D}/${sbindir}/
    install -m 0755 ${S}power-util ${D}/${sbindir}/
    install -m 0755 ${S}power_led.sh ${D}/${sbindir}/
    install -m 0755 ${S}ast-functions ${D}/${sbindir}/
}
