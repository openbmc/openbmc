KERNEL_FEATURES += "cfg/efi-ext.scc"

inherit sbsign

# shell variable set inside do_compile task
SBSIGN_TARGET_BINARY = "$KERNEL_IMAGE"

do_compile:append() {
    KERNEL_IMAGE=$(find ${B} -name ${KERNEL_IMAGETYPE} -print -quit)
    do_sbsign
}

RRECOMMENDS:${PN} += "kernel-module-efivarfs"
RRECOMMENDS:${PN} += "kernel-module-efivars"
