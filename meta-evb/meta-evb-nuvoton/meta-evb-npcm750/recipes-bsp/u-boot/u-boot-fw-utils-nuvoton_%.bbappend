FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:evb-npcm750  = " file://fw_env.config"

do_install:append:evb-npcm750 () {
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
