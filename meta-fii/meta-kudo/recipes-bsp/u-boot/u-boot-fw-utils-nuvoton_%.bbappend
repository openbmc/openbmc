FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"

SRC_URI_append_kudo = " file://fw_env.config"

do_install_append_kudo () {
  install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
