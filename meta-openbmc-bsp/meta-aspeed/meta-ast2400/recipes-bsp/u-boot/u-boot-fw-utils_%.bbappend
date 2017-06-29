FILESEXTRAPATHS_prepend_ast2400 := "${THISDIR}/files:"

SRC_URI_append_ast2400 = " file://fw_env.config"

do_install_append_ast2400() {
    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi
}

BBCLASSEXTEND += "native"
