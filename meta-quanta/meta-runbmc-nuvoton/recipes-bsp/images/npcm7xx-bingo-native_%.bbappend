FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_remove = "file://BootBlockAndHeader_EB.xml"
SRC_URI_remove = "file://UbootHeader_EB.xml"
SRC_URI += " file://BootBlockAndHeader_RunBMC.xml"
SRC_URI += " file://UbootHeader_RunBMC.xml"


do_install_append() {
	install ${WORKDIR}/BootBlockAndHeader_RunBMC.xml ${D}${bindir}/BootBlockAndHeader_EB.xml
	install ${WORKDIR}/UbootHeader_RunBMC.xml ${D}${bindir}/UbootHeader_EB.xml
}

