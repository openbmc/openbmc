FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://power-config-host0.json"
SRC_URI:append:olympus-nuvoton = " file://0001-support-host-boot-progress.patch"
SRC_URI:append:olympus-nuvoton = " file://0002-add-support-chassis-on-off-target-files.patch"
SRC_URI:append:olympus-nuvoton = " file://obmc-chassis-poweroff.target"
SRC_URI:append:olympus-nuvoton = " file://obmc-chassis-poweron.target"

SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " obmc-chassis-poweroff.target"
SYSTEMD_SERVICE:${PN}:append:olympus-nuvoton = " obmc-chassis-poweron.target"

inherit obmc-phosphor-systemd

FILES:${PN}:append:olympus-nuvoton = " ${datadir}/x86-power-control/power-config-host0.json"

do_install:append:olympus-nuvoton() {
    install -d ${D}${datadir}/x86-power-control
    install -m 0644 -D ${WORKDIR}/power-config-host0.json \
        ${D}${datadir}/x86-power-control/power-config-host0.json
}
