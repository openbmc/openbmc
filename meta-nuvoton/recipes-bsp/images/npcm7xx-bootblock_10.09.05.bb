SUMMARY = "Primary bootloader for NPCM7XX (Poleg) devices"
DESCRIPTION = "Primary bootloader for NPCM7XX (Poleg) devices"
HOMEPAGE = "https://github.com/Nuvoton-Israel/npcm7xx-bootblock"
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRCREV = "${PV}"
FILENAME = "Poleg_bootblock.bin"

SRC_URI = "git://github.com/Nuvoton-Israel/npcm7xx-bootblock;protocol=git"
SRC_URI[md5sum] = "cf8daa5f4636ed1ff952618e435af028"

S = "${WORKDIR}/git"

inherit deploy

do_deploy () {
	install -d ${DEPLOYDIR}
	install -m 644 ${FILENAME} ${DEPLOYDIR}/
}

addtask deploy before do_build after do_compile
