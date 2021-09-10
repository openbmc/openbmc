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

RDEPENDS:packagegroup-core-security = "\
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-audit \
    packagegroup-security-hardening \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-security-ptest-packages", "", d)} \
    "

SUMMARY:packagegroup-security-utils = "Security utilities"
RDEPENDS:packagegroup-security-utils = "\
    checksec \
    ding-libs \
    ecryptfs-utils \
    fscryptctl \
    keyutils \
    nmap \
    pinentry \
    python3-privacyidea \
    python3-fail2ban \
    softhsm \
    libest \
    opendnssec \
    sshguard \
    ${@bb.utils.contains_any("TUNE_FEATURES", "riscv32 ", "", " libseccomp",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sssd google-authenticator-libpam", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pax", "pax-utils packctl", "",d)} \
    "

SUMMARY:packagegroup-security-scanners = "Security scanners"
RDEPENDS:packagegroup-security-scanners = "\
    isic \
    nikto \
    checksecurity \
    ${@bb.utils.contains_any("TUNE_FEATURES", "riscv32 riscv64", "", " clamav clamav-daemon clamav-freshclam",d)} \
    "
RDEPENDS:packagegroup-security-scanners:remove:libc-musl = "clamav clamav-daemon clamav-freshclam"

SUMMARY:packagegroup-security-audit = "Security Audit tools "
RDEPENDS:packagegroup-security-audit = " \
    buck-security \
    redhat-security \
    "

SUMMARY:packagegroup-security-hardening = "Security Hardening tools"
RDEPENDS:packagegroup-security-hardening = " \
    bastille \
    "

SUMMARY:packagegroup-security-ids = "Security Intrusion Detection systems"
RDEPENDS:packagegroup-security-ids = " \
    samhain-standalone \
    ${@bb.utils.contains("BBLAYERS", "meta-rust", "suricata","", d)} \
    ossec-hids \
    aide \
    "

RDEPENDS:packagegroup-security-ids:remove:powerpc = "suricata"
RDEPENDS:packagegroup-security-ids:remove:powerpc64le = "suricata"
RDEPENDS:packagegroup-security-ids:remove:powerpc64 = "suricata"
RDEPENDS:packagegroup-security-ids:remove:riscv32 = "suricata"
RDEPENDS:packagegroup-security-ids:remove:riscv64 = "suricata"
RDEPENDS:packagegroup-security-ids:remove:libc-musl = "ossec-hids"

SUMMARY:packagegroup-security-mac = "Security Mandatory Access Control systems"
RDEPENDS:packagegroup-security-mac = " \
    ${@bb.utils.contains("DISTRO_FEATURES", "tomoyo", "ccs-tools", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack", "",d)} \
    "

RDEPENDS:packagegroup-security-mac:remove:mipsarch = "apparmor"

RDEPENDS:packagegroup-meta-security-ptest-packages = "\
    ptest-runner \
    samhain-standalone-ptest \
    ${@bb.utils.contains("BBLAYERS", "meta-rust", "suricata-ptest","", d)} \
    python3-fail2ban-ptest \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-ptest", "",d)} \
"

RDEPENDS:packagegroup-security-ptest-packages:remove:powerpc = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:powerpc64le = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:powerpc64 = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:riscv32 = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:riscv64 = "suricata-ptest"
