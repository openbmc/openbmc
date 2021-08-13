FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://fw_env.config"

do_install:append () {
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
