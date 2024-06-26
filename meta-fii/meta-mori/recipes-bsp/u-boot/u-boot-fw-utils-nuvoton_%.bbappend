FILESEXTRAPATHS:prepend:mori := "${THISDIR}/${PN}:"

SRC_URI:append:mori = " file://fw_env.config"

do_install:append:mori () {
  install -m 644 ${UNPACKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
