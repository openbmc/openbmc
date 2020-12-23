DESCRIPTION = "Security packagegroup for Poky"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup

PACKAGES = "\
    packagegroup-core-security \
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-audit \
    packagegroup-security-hardening \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-security-ptest-packages", "", d)} \
    "

RDEPENDS_packagegroup-core-security = "\
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-audit \
    packagegroup-security-hardening \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-security-ptest-packages", "", d)} \
    "

SUMMARY_packagegroup-security-utils = "Security utilities"
RDEPENDS_packagegroup-security-utils = "\
    checksec \
    ding-libs \
    ecryptfs-utils \
    fscryptctl \
    keyutils \
    nmap \
    pinentry \
    python3-privacyidea \
    python3-fail2ban \
    python3-scapy \
    softhsm \
    libest \
    opendnssec \
    ${@bb.utils.contains_any("TUNE_FEATURES", "riscv32 ", "", " libseccomp",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sssd google-authenticator-libpam", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pax", "pax-utils packctl", "",d)} \
    "

SUMMARY_packagegroup-security-scanners = "Security scanners"
RDEPENDS_packagegroup-security-scanners = "\
    isic \
    nikto \
    checksecurity \
    ${@bb.utils.contains_any("TUNE_FEATURES", "riscv32 riscv64", "", " clamav clamav-freshclam clamav-cvd",d)} \
    "
RDEPENDS_packagegroup-security-scanners_remove_libc-musl = "clamav clamav-freshclam clamav-cvd"

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
    ${@bb.utils.contains_any("TUNE_FEATURES", "ppc7400 riscv32 riscv64", "", " suricata",d)} \
    "

SUMMARY_packagegroup-security-mac = "Security Mandatory Access Control systems"
RDEPENDS_packagegroup-security-mac = " \
    ${@bb.utils.contains("DISTRO_FEATURES", "tomoyo", "ccs-tools", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack", "",d)} \
    "

RDEPENDS_packagegroup-meta-security-ptest-packages = "\
    ptest-runner \
    samhain-standalone-ptest \
    libseccomp-ptest \
    python3-scapy-ptest \
    suricata-ptest \
    tripwire-ptest \
    python3-fail2ban-ptest \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-ptest", "",d)} \
"
