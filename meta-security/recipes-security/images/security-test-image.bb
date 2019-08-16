DESCRIPTION = "A small image for testing meta-security packages"

IMAGE_FEATURES += "ssh-server-openssh"

TEST_SUITES = "ssh ping ptest apparmor clamav samhain sssd tripwire checksec smack suricata"

INSTALL_CLAMAV_CVD = "1"

IMAGE_INSTALL = "\
    packagegroup-base \
    packagegroup-core-boot \
    packagegroup-core-security-ptest \
    clamav \
    tripwire \
    checksec \
    suricata \
    samhain-standalone \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sssd", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "apparmor", "apparmor", "",d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-test", "",d)} \
    os-release \
    " 


IMAGE_LINGUAS ?= " "

LICENSE = "MIT"

inherit core-image

export IMAGE_BASENAME = "security-test-image"

IMAGE_ROOTFS_EXTRA_SPACE = "5242880"
