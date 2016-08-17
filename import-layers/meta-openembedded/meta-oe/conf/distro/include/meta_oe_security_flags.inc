# Build errors with the pie options enabled
SECURITY_CFLAGS_pn-lvm2 = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-rrdtool = "${SECURITY_NO_PIE_CFLAGS}"

# This has text reloc errors with the pie options enabled
SECURITY_CFLAGS_pn-mozjs = "${SECURITY_NO_PIE_CFLAGS}"
SECURITY_CFLAGS_pn-openldap = "${SECURITY_NO_PIE_CFLAGS}"
