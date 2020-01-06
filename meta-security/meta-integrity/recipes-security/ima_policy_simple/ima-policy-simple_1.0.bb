SUMMARY = "IMA sample simple policy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# This policy file will get installed as /etc/ima/ima-policy.
# It is located via the normal file search path, so a .bbappend
# to this recipe can just point towards one of its own files.
IMA_POLICY ?= "ima_policy_simple"

SRC_URI = " file://${IMA_POLICY}"

inherit features_check
REQUIRED_DISTRO_FEATURES = "ima"

do_install () {
    install -d ${D}/${sysconfdir}/ima
    install ${WORKDIR}/${IMA_POLICY}  ${D}/${sysconfdir}/ima/ima-policy
}

FILES_${PN} = "${sysconfdir}/ima"
RDEPENDS_${PN} = "ima-evm-utils"
