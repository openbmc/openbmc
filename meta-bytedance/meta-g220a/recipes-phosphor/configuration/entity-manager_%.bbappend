FILESEXTRAPATHS_append := ":${THISDIR}/${PN}"
SRC_URI_append = " file://g220a_baseboard.json \
		"
do_install_append() {
     install -d ${D}/usr/share/entity-manager/configurations
     install -m 0444 ${WORKDIR}/*.json ${D}/usr/share/entity-manager/configurations
}
