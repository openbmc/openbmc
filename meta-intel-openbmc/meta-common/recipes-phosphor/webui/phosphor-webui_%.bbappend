FILESEXTRAPATHS:prepend:intel := "${THISDIR}/${PN}:"

SRC_URI:append:intel = " file://config.json"

do_compile:prepend:intel() {
        cp -r ${WORKDIR}/config.json ${S}/
}

