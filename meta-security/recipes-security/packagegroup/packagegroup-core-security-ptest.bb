DESCRIPTION = "Security ptest packagegroup"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGES = "\
    ${PN} \
    "

ALLOW_EMPTY_${PN} = "1"

SUMMARY_${PN} = "Security packages with ptests"
RDEPENDS_${PN} = " \
    ptest-runner \
    samhain-standalone-ptest \
    xmlsec1-ptest \
    keyutils-ptest \
    libseccomp-ptest \
    python-scapy-ptest \
    suricata-ptest \
    tripwire-ptest \
    python-fail2ban-ptest \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor-ptest", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-ptest", "",d)} \
    "
