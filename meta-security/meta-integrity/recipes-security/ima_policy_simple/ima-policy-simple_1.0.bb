SUMMARY = "IMA sample simple policy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " file://ima_policy_simple"

S = "${UNPACKDIR}"

inherit features_check
REQUIRED_DISTRO_FEATURES = "ima"

do_install () {
    install -d ${D}/${sysconfdir}/ima
    install ${UNPACKDIR}/ima_policy_simple ${D}/${sysconfdir}/ima/ima-policy
}

FILES:${PN} = "${sysconfdir}/ima"
RDEPENDS:${PN} = "ima-evm-utils"
