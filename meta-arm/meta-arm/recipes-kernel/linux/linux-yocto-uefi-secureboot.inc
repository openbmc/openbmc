KERNEL_FEATURES += "cfg/efi-ext.scc"

RRECOMMENDS:${PN} += "kernel-module-efivarfs"
RRECOMMENDS:${PN} += "kernel-module-efivars"
