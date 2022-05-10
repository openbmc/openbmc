FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm845  = " file://fw_env.config"

do_install:append:evb-npcm845 () {
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
