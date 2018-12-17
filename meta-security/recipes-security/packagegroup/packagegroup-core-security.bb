DESCRIPTION = "Security packagegroup for Poky"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

PACKAGES = "\
    packagegroup-core-security \
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    ${@bb.utils.contains("MACHINE_FEATURES", "tpm", "packagegroup-security-tpm", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-security-ptest", "", d)} \
    "

RDEPENDS_packagegroup-core-security = "\
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    ${@bb.utils.contains("MACHINE_FEATURES", "tpm", "packagegroup-security-tpm", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-security-ptest", "", d)} \
    "

SUMMARY_packagegroup-security-utils = "Security utilities"
RDEPENDS_packagegroup-security-utils = "\
    checksec \
    nmap \
    pinentry \
    python-scapy \
    ding-libs \
    xmlsec1 \
    keyutils \
    libseccomp \
    ${@bb.utils.contains("DISTRO_FEATURES", "pax", "pax-utils", "",d)} \
    "

SUMMARY_packagegroup-security-scanners = "Security scanners"
RDEPENDS_packagegroup-security-scanners = "\
    nikto \
    checksecurity \
    clamav \
    "

SUMMARY_packagegroup-security-audit = "Security Audit tools "
RDEPENDS_packagegroup-security-audit = " \
    buck-security \
    redhat-security \
    "

SUMMARY_packagegroup-security-hardening = "Security Hardening tools"
RDEPENDS_packagegroup-security-hardening = " \
    bastille \
    "

SUMMARY_packagegroup-security-ids = "Security Intrusion Detection systems"
RDEPENDS_packagegroup-security-ids = " \
    tripwire \
    samhain-standalone \
    suricata \
    "

SUMMARY_packagegroup-security-mac = "Security Mandatory Access Control systems"
RDEPENDS_packagegroup-security-mac = " \
    ${@bb.utils.contains("DISTRO_FEATURES", "tomoyo", "ccs-tools", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack", "",d)} \
    "

SUMMARY_packagegroup-security-ptest = "Security packages with ptests"
RDEPENDS_packagegroup-security-ptest = " \
    samhain-standalone-ptest \
    xmlsec1-ptest \
    keyutils-ptest \
    libseccomp-ptest \
    python-scapy-ptest \
    suricata-ptest \
    tripwire-ptest \
    python3-fail2ban-ptest \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor-ptest", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-ptest", "",d)} \
    ptest-runner \
    "
