FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://power-config-host0.json"

do_configure:prepend() {
cp ${WORKDIR}/power-config-host0.json ${S}/config
}


