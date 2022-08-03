LINUX_ARM64_ACK_TOOLCHAIN_REQUIRE = "${@oe.utils.ifelse(d.getVar('LINUX_ACK_TOOLCHAIN_CLANG'), 'linux-arm64-ack-clang.inc', '')}"

require ${LINUX_ARM64_ACK_TOOLCHAIN_REQUIRE}
