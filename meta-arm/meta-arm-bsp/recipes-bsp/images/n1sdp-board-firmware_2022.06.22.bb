SUMMARY = "Board Firmware binaries for N1SDP"
SECTION = "firmware"

LICENSE = "STM-SLA0044-Rev5"
LIC_FILES_CHKSUM = "file://LICENSES/MB/STM.TXT;md5=1b74d8c842307d03c116f2d71cbf868a"

inherit deploy

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "n1sdp"

SRC_URI = "git://git.gitlab.arm.com/arm-reference-solutions/board-firmware.git;protocol=https;branch=n1sdp"

SRCREV = "70ba494265eee76747faff38264860c19e214540"
PV .= "+git"

S = "${WORKDIR}/git"

INSTALL_DIR = "/n1sdp-board-firmware_source"

do_install() {
    rm -rf ${S}/SOFTWARE
    install -d ${D}${INSTALL_DIR}
    cp -Rp --no-preserve=ownership ${S}/* ${D}${INSTALL_DIR}
}

FILES:${PN}-staticdev += " ${INSTALL_DIR}/LIB/sensor.a"
FILES:${PN} = "${INSTALL_DIR}"
SYSROOT_DIRS += "${INSTALL_DIR}"

do_deploy() {
    install -d ${DEPLOYDIR}${INSTALL_DIR}
    cp -Rp --no-preserve=ownership ${S}/* ${DEPLOYDIR}${INSTALL_DIR}
}
addtask deploy after do_install before do_build
