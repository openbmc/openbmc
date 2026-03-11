DESCRIPTION = "A small image for testing meta-security packages"

require security-build-image.bb

IMAGE_FEATURES += "ssh-server-openssh"

IMAGE_INSTALL:append = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "smack", "smack-test", "",d)} \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "tpm-layer", "packagegroup-security-tpm","", d)} \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "tpm-layer", "packagegroup-security-tpm2","", d)} \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "parsec-layer", "packagegroup-security-parsec","", d)} \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "integrity", "packagegroup-ima-evm-utils","", d)} \
"

TEST_SUITES = "ssh ping apparmor clamav samhain sssd checksec smack suricata aide firejail"
TEST_SUITES:append = " parsec tpm2 swtpm ima"

INSTALL_CLAMAV_CVD = "1"

IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "1124288"

# ptests need more memory than standard to avoid the OOM killer
# also lttng-tools needs /tmp that has at least 1G
QB_MEM = "-m 2048"

PTEST_EXPECT_FAILURE = "1"
