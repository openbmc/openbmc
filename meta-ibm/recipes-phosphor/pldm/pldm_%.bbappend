FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

EXTRA_OEMESON_append = " \
        -Doem-ibm=enabled \
        "

SRC_URI += "file://fileTable.json"
SRC_URI += "file://enum_attrs.json"
SRC_URI += "file://11.json"
SRC_URI += "file://host_eid"

do_install_append() {
        install -d ${D}${datadir}/pldm/bios/
        install -d ${D}${datadir}/pldm/pdr/
        install -m 0644 ${WORKDIR}/fileTable.json ${D}${datadir}/pldm/
        install -m 0644 ${WORKDIR}/enum_attrs.json ${D}${datadir}/pldm/bios/
        install -m 0644 ${WORKDIR}/11.json ${D}${datadir}/pldm/pdr/
        install -m 0644 ${WORKDIR}/host_eid ${D}${datadir}/pldm/
}
