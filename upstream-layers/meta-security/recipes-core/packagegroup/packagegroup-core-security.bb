DESCRIPTION = "Security packagegroup for Poky"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PACKAGES = "\
    packagegroup-core-security \
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-audit \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    packagegroup-security-compliance  \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-security-ptest-packages", "", d)} \
    "

RDEPENDS:packagegroup-core-security = "\
    packagegroup-security-utils \
    packagegroup-security-scanners \
    packagegroup-security-audit \
    packagegroup-security-ids  \
    packagegroup-security-mac  \
    packagegroup-security-compliance  \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-security-ptest-packages", "", d)} \
    "

SUMMARY:packagegroup-security-utils = "Security utilities"
RDEPENDS:packagegroup-security-utils = "\
    bubblewrap \
    checksec \
    cryptmount \
    ding-libs \
    ecryptfs-utils \
    fscryptctl \
    glome \
    keyutils \
    libgssglue \
    libmhash \
    nmap \
    pinentry \
    softhsm \
    sshguard \
    ${@bb.utils.contains("DISTRO_FEATURES", "seccomp ", "libseccomp", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "google-authenticator-libpam", "",d)} \
    "

have_krill =  "${@bb.utils.contains("DISTRO_FEATURES", "pam", "krill", "",d)}"
RDEPENDS:packagegroup-security-utils:append:x86 = " ${have_krill}"
RDEPENDS:packagegroup-security-utils:append:x86-64 = " firejail ${have_krill}"
RDEPENDS:packagegroup-security-utils:append:aarch64 = " firejail ${have_krill}"
RDEPENDS:packagegroup-security-utils:remove:libc-musl = "krill firejail"

ARPWATCH = "arpwatch"
ARPWATCH:riscv32 = ""
ARPWATCH:riscv64 = ""
CLAMAV = "clamav clamav-daemon clamav-freshclam"
CLAMAV:arm = ""
CLAMAV:mips = ""
CLAMAV:powerpc = ""
CLAMAV:riscv32 = ""
CLAMAV:riscv64 = ""
CLAMAV:x86 = ""

SUMMARY:packagegroup-security-scanners = "Security scanners"
RDEPENDS:packagegroup-security-scanners = "\
    ${ARPWATCH} \
    chkrootkit \
    isic \
    ${CLAMAV} \
    "

RDEPENDS:packagegroup-security-scanners:remove:libc-musl = "clamav clamav-daemon clamav-freshclam"
RDEPENDS:packagegroup-security-scanners:remove:libc-musl = "arpwatch"

SUMMARY:packagegroup-security-audit = "Security Audit tools "
RDEPENDS:packagegroup-security-audit = " \
    buck-security \
    redhat-security \
    "

SUMMARY:packagegroup-security-ids = "Security Intrusion Detection systems"
RDEPENDS:packagegroup-security-ids = " \
    samhain-standalone \
    suricata \
    python3-suricata-update \
    ossec-hids \
    aide \
    "

RDEPENDS:packagegroup-security-ids:remove:powerpc = "suricata python3-suricata-update"
RDEPENDS:packagegroup-security-ids:remove:powerpc64le = "suricata python3-suricata-update"
RDEPENDS:packagegroup-security-ids:remove:powerpc64 = "suricata python3-suricata-update"
RDEPENDS:packagegroup-security-ids:remove:riscv32 = "suricata python3-suricata-update"
RDEPENDS:packagegroup-security-ids:remove:riscv64 = "suricata python3-suricata-update"
RDEPENDS:packagegroup-security-ids:remove:libc-musl = "ossec-hids"
RDEPENDS:packagegroup-security-ids:remove:libc-musl = "aide"

SUMMARY:packagegroup-security-mac = "Security Mandatory Access Control systems"
RDEPENDS:packagegroup-security-mac = " \
    ${@bb.utils.contains("DISTRO_FEATURES", "tomoyo", "ccs-tools", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack", "",d)} \
    "

RDEPENDS:packagegroup-security-mac:remove:mipsarch = "apparmor"

SUMMARY:packagegroup-security-compliance = "Security Compliance applications"
RDEPENDS:packagegroup-security-compliance = " \
    lynis \
    openscap \
    scap-security-guide \
    os-release \
    "

RDEPENDS:packagegroup-security-compliance:remove:libc-musl = "openscap scap-security-guide"

RDEPENDS:packagegroup-meta-security-ptest-packages = "\
    ptest-runner \
    samhain-standalone-ptest \
    ${@bb.utils.contains("BBLAYERS", "meta-rust", "suricata-ptest","", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-ptest", "",d)} \
"

RDEPENDS:packagegroup-security-ptest-packages:remove:powerpc = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:powerpc64le = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:powerpc64 = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:riscv32 = "suricata-ptest"
RDEPENDS:packagegroup-security-ptest-packages:remove:riscv64 = "suricata-ptest"
