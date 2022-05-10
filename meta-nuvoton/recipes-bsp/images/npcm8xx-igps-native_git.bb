SUMMARY = "Image Generation and Programming Scripts for NPCM8XX (Arbel) devices"
DESCRIPTION = "Image Generation and Programming Scripts for NPCM8XX (Arbel) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/igps"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = " \
    git://github.com/Nuvoton-Israel/igps;branch=master;protocol=https \
    file://KmtAndHeader_${IGPS_MACHINE}.xml \
    file://TipFwAndHeader_L0_${IGPS_MACHINE}.xml \
    file://TipFwAndHeader_L1_${IGPS_MACHINE}.xml \
    file://BootBlockAndHeader_${IGPS_MACHINE}.xml \
    file://BL31_AndHeader_${IGPS_MACHINE}.xml \
    file://OpTeeAndHeader_${IGPS_MACHINE}.xml \
    file://UbootHeader_${IGPS_MACHINE}.xml \
"

# tag IGPS_02.01.12
SRCREV = "2fb1a3b0d61164ed1157e27889a4ec2292cbc760"

S = "${WORKDIR}/git"

DEST = "${D}${datadir}/${BPN}"

do_install() {
	install -d ${DEST}
	install ${WORKDIR}/KmtAndHeader_${IGPS_MACHINE}.xml ${DEST}
	install ${WORKDIR}/TipFwAndHeader_L0_${IGPS_MACHINE}.xml ${DEST}
	install ${WORKDIR}/TipFwAndHeader_L1_${IGPS_MACHINE}.xml ${DEST}
	install ${WORKDIR}/BootBlockAndHeader_${IGPS_MACHINE}.xml ${DEST}
	install ${WORKDIR}/BL31_AndHeader_${IGPS_MACHINE}.xml ${DEST}
	install ${WORKDIR}/OpTeeAndHeader_${IGPS_MACHINE}.xml ${DEST}
	install ${WORKDIR}/UbootHeader_${IGPS_MACHINE}.xml ${DEST}
}

inherit native
