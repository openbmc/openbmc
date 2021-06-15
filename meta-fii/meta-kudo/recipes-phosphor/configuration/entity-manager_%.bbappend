FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"

SRC_URI_append_kudo = " \
    file://kudo.json \
    file://blacklist.json \
    "

do_install_append_kudo () {
    install -m 0644 -D ${WORKDIR}/kudo.json ${D}${datadir}/${PN}/configurations/kudo.json
    install -m 0644 -D ${WORKDIR}/blacklist.json ${D}${datadir}/${PN}/blacklist.json
}
