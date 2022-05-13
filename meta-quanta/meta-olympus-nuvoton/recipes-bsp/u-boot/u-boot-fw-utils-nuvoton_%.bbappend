FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton  = " file://fw_env.config"

do_install:append:olympus-nuvoton () {
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
