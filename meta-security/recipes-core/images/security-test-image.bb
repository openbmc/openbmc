DESCRIPTION = "A small image for testing meta-security packages"

require security-build-image.bb

IMAGE_FEATURES += "ssh-server-openssh"

TEST_SUITES = "ssh ping ptest apparmor clamav samhain sssd tripwire checksec smack suricata"

INSTALL_CLAMAV_CVD = "1"

IMAGE_OVERHEAD_FACTOR = "1.0"
IMAGE_ROOTFS_EXTRA_SPACE = "1124288"

# ptests need more memory than standard to avoid the OOM killer
# also lttng-tools needs /tmp that has at least 1G
QB_MEM = "-m 2048"

PTEST_EXPECT_FAILURE = "1"
