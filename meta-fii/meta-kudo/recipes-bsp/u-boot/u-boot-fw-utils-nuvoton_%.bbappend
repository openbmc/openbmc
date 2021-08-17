FILESEXTRAPATHS:prepend:kudo := "${THISDIR}/${PN}:"

SRC_URI:append:kudo = " file://fw_env.config"

do_install:append:kudo () {
  install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
