DESCRIPTION = "Security packagegroup for Poky"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

PACKAGES = "packagegroup-security-vtpm"

SUMMARY:packagegroup-security-vtpm = "Security Software vTPM support"
RDEPENDS:packagegroup-security-vtpm = " \
    libtpms \
    swtpm \
    "		
