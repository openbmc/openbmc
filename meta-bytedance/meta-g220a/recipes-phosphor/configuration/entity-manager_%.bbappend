FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
SRC_URI:append = " file://g220a_baseboard.json \
		"
do_install:append() {
     install -d ${D}${datadir}/entity-manager/configurations
     install -m 0444 ${UNPACKDIR}/*.json ${D}${datadir}/entity-manager/configurations
}
