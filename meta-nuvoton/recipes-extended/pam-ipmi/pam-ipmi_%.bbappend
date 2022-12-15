FILESEXTRAPATHS:prepend:npcm8xx := "${THISDIR}/${PN}:"

SRC_URI:append:npcm8xx= " file://ipmi_pass_64"

do_install:append:npcm8xx() {
    install -d ${D}${sysconfdir}
    rm -f ${D}${sysconfdir}/ipmi_pass
    install -m 0600 ${WORKDIR}/ipmi_pass_64 ${D}${sysconfdir}/ipmi_pass
}
