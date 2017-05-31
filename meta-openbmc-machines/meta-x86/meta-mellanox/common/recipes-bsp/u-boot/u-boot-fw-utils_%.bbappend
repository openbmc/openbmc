FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://fw_env.config"

do_install_append() {
    if [ -e ${WORKDIR}/fw_env.config ] ; then
        install -d ${D}${sysconfdir}
        install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
    fi
}

BBCLASSEXTEND += "native"
