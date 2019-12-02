require emlog.inc

inherit module

EXTRA_OEMAKE += " \
    KDIR=${STAGING_KERNEL_DIR} \
    KVER=${KERNEL_VERSION} \
"

do_compile() {
    oe_runmake modules
}
