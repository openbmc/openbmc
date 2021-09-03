# Build errors with PIE options enabled
SECURITY_CFLAGS:pn-rust-native = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS:pn-rust-cross-${TARGET_ARCH} = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS:pn-rust = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS:pn-rust-llvm = "${SECURITY_NO_PIE_CFLAGS}"

SECURITY_LDFLAGS:pn-rust-cross-arm = " -lssp_nonshared -lssp"
