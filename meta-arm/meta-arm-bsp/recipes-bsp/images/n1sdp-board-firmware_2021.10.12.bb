SUMMARY = "Board Firmware binaries for N1SDP"
SECTION = "firmware"

LICENSE = "STM-SLA0044-Rev5"
LIC_FILES_CHKSUM = "file://LICENSES/STM.TXT;md5=cd18335eff80d0a690a650f0e6748baf"

inherit deploy

INHIBIT_DEFAULT_DEPS = "1"

PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_MACHINE = "n1sdp"

SRC_URI = "git://git.gitlab.arm.com/arm-reference-solutions/board-firmware.git;protocol=https;branch=n1sdp"

SRCREV = "e6cd91c7a9733e501bc3b57ff6f9eb2461ffee54"

S = "${WORKDIR}/git"

INSTALL_DIR = "/n1sdp-board-firmware_source"

do_install() {
    rm -rf ${S}/SOFTWARE
    install -d ${D}${INSTALL_DIR}
    cp -Rp --no-preserve=ownership ${S}/* ${D}${INSTALL_DIR}
}

FILES:${PN} = "${INSTALL_DIR}"
SYSROOT_DIRS += "${INSTALL_DIR}"

do_deploy() {
    install -d ${DEPLOYDIR}${INSTALL_DIR}
    cp -Rp --no-preserve=ownership ${S}/* ${DEPLOYDIR}${INSTALL_DIR}
}
addtask deploy after do_install before do_build
