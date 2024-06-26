FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://power-config-host0.json"

do_configure:prepend() {
cp ${UNPACKDIR}/power-config-host0.json ${S}/config
}


