FILESEXTRAPATHS:prepend:gsj := "${THISDIR}/${PN}:"

SRC_URI:append:gsj = " file://fw_env.config"

do_install:append:gsj () {
	install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
