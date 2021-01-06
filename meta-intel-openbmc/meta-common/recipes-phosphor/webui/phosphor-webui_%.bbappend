FILESEXTRAPATHS_prepend_intel := "${THISDIR}/${PN}:"

SRC_URI_append_intel = " file://config.json"

do_compile_prepend_intel() {
        cp -r ${WORKDIR}/config.json ${S}/
}

