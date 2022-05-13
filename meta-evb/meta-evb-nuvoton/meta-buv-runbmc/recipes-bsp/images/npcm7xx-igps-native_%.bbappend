FILESEXTRAPATHS:prepend:buv-runbmc :=  "${THISDIR}/file:"

SRC_URI:append:buv-runbmc = " file://BootBlockAndHeader_RunBMC.xml"
SRC_URI:append:buv-runbmc = " file://UbootHeader_RunBMC.xml"

do_install:append:buv-runbmc() {
	install -d ${DEST}
	install -m 0644 ${WORKDIR}/BootBlockAndHeader_RunBMC.xml ${DEST}/BootBlockAndHeader_${IGPS_MACHINE}.xml
	install -m 0644 ${WORKDIR}/UbootHeader_RunBMC.xml ${DEST}/UbootHeader_${IGPS_MACHINE}.xml 
}
