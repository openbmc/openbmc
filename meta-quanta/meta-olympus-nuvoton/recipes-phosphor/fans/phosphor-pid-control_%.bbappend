inherit entity-utils
FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://config-olympus-nuvoton.json"
SRC_URI:append:olympus-nuvoton = " file://fan-default-speed.sh"
SRC_URI:append:olympus-nuvoton = " file://phosphor-pid-control-olympus.service"
SRC_URI:append:olympus-nuvoton = " file://phosphor-pid-control-stop.service"
#SRC_URI:append:olympus-nuvoton = " file://phosphor-pid-control-bootcheck.service"
SRC_URI:append:olympus-nuvoton = " file://fan-reboot-control.service"
SRC_URI:append:olympus-nuvoton = " file://fan-boot-control.service"

FILES:${PN}:append:olympus-nuvoton = " ${bindir}/fan-default-speed.sh"
FILES:${PN}:append:olympus-nuvoton = " ${datadir}/swampd/config.json"

RDEPENDS:${PN} += "bash"

SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " phosphor-pid-control-stop.service"
#SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " phosphor-pid-control-bootcheck.service"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " fan-reboot-control.service"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " fan-boot-control.service"

inherit obmc-phosphor-systemd

PID_TMPL = "phosphor-pid-control.service"
CHASSIS_POWERON_TGTFMT = "obmc-chassis-poweron.target"
ENABLE_PID_FMT = "../${PID_TMPL}:${CHASSIS_POWERON_TGTFMT}.wants/${PID_TMPL}"
SYSTEMD_LINK:${PN}:append:olympus-nuvoton = " ${@compose_list(d, 'ENABLE_PID_FMT', 'OBMC_CHASSIS_INSTANCES')}"

PID_STOP_TMPL = "phosphor-pid-control-stop.service"
CHASSIS_POWEROFF_TGTFMT = "obmc-chassis-poweroff.target"
DISABLE_PID_FMT = "../${PID_STOP_TMPL}:${CHASSIS_POWEROFF_TGTFMT}.wants/${PID_STOP_TMPL}"
SYSTEMD_LINK:${PN}:append:olympus-nuvoton = " ${@compose_list(d, 'DISABLE_PID_FMT', 'OBMC_CHASSIS_INSTANCES')}"

do_install:append:olympus-nuvoton() {
    install -d ${D}/${bindir}
    install -m 0755 ${WORKDIR}/fan-default-speed.sh ${D}/${bindir}
    install -d ${D}${systemd_unitdir}/system

    if [ "${DISTRO}" != "olympus-entity" ];then
        install -d ${D}${datadir}/swampd
        install -m 0644 -D ${WORKDIR}/config-olympus-nuvoton.json \
            ${D}${datadir}/swampd/config.json
        install -m 0644 ${WORKDIR}/phosphor-pid-control-olympus.service \
            ${D}${systemd_unitdir}/system/phosphor-pid-control.service
    fi

    install -m 0644 ${WORKDIR}/phosphor-pid-control-stop.service \
        ${D}${systemd_unitdir}/system
    #install -m 0644 ${WORKDIR}/phosphor-pid-control-bootcheck.service \
    #    ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-reboot-control.service \
        ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/fan-boot-control.service \
        ${D}${systemd_unitdir}/system
}
