FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " file://config.json"

do_compile_prepend() {
        cp -r ${WORKDIR}/config.json ${S}/
}

