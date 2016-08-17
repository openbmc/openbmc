# Build errors with the pie options enabled
SECURITY_CFLAGS_pn-libvirt = "${SECURITY_NO_PIE_CFLAGS}"

# This has text relco errors with the pie options enabled
SECURITY_CFLAGS_pn-lxc = "${SECURITY_NO_PIE_CFLAGS}"
