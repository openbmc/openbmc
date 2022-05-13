FILESEXTRAPATHS:prepend:olympus-nuvoton :=  "${THISDIR}/file:"

SRC_URI:append:olympus-nuvoton = " file://BootBlockAndHeader_RunBMC.xml"
SRC_URI:append:olympus-nuvoton = " file://UbootHeader_RunBMC.xml"

do_install:append:olympus-nuvoton() {
	install -d ${DEST}
	install -m 0644 ${WORKDIR}/BootBlockAndHeader_RunBMC.xml ${DEST}/BootBlockAndHeader_${IGPS_MACHINE}.xml
	install -m 0644 ${WORKDIR}/UbootHeader_RunBMC.xml ${DEST}/UbootHeader_${IGPS_MACHINE}.xml 
}
