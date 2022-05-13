FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " file://default \
             file://host_eid \
             file://0001-support-sensor-reading.patch "

CONFFILES:${PN}:olympus-nuvoton = "${sysconfdir}/default/pldmd"
FILES:${PN}:append:olympus-nuvoton = " ${datadir}/host_eid"

do_install:append:olympus-nuvoton() {
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${WORKDIR}/default ${D}${sysconfdir}/default/pldmd
	install -d ${D}${datadir}/pldm
	install -m 0644 -D ${WORKDIR}/host_eid ${D}${datadir}/pldm/host_eid
}
