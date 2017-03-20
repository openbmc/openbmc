FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://extra-properties.yaml"

do_install_append() {

        install extra-properties.yaml ${DEST}/out.yaml
}

