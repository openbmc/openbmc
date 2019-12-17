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
    "

RDEPENDS_packagegroup-core-security = "\
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    "

SUMMARY_packagegroup-security-utils = "Security utilities"
RDEPENDS_packagegroup-security-utils = "\
    checksec \
    nmap \
    pinentry \
    python3-scapy \
    ding-libs \
    keyutils \
    libseccomp \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sssd", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pax", "pax-utils", "",d)} \
    "

SUMMARY_packagegroup-security-scanners = "Security scanners"
RDEPENDS_packagegroup-security-scanners = "\
    nikto \
    checksecurity \
    clamav \
    clamav-freshclam \
    clamav-cvd \
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
