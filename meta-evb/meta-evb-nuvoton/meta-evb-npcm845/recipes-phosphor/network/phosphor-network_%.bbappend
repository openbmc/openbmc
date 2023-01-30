FILESEXTRAPATHS:prepend:evb-npcm845 := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove:evb-npcm845 = "default-link-local-autoconf"

SRC_URI:append:evb-npcm845 = " file://default.link.conf"

do_install:append:evb-npcm845() {
        install -m 0755 -d ${D}/etc/systemd/network/99-default.link.d
        install -m 0644 ${WORKDIR}/default.link.conf ${D}/etc/systemd/network/99-default.link.d
}
