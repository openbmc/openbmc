FILESEXTRAPATHS:prepend:evb-npcm845:= "${THISDIR}/files:"

SRC_URI:append:evb-npcm845= " \
    file://0001-networkd-create-new-socket.patch \
    file://systemd-networkd-wait-online.service \
"

SYSTEMD_SERVICE:${PN}:append:evb-npcm845 = " systemd-networkd-wait-online.service"

do_install:append:evb-npcm845() {
    install -d ${D}${systemd_unitdir}/system/
    install -m 0644 ${WORKDIR}/systemd-networkd-wait-online.service \
        ${D}${systemd_unitdir}/system

}
