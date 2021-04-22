FILESEXTRAPATHS_prepend_kudo := "${THISDIR}/${PN}:"

SRC_URI_append_kudo = " file://kudo.json"

do_install_append_kudo () {
        install -m 0644 -D ${WORKDIR}/kudo.json ${D}/usr/share/entity-manager/configurations/kudo.json
}
