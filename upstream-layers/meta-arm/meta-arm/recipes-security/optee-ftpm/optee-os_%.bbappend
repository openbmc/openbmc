FTPM_UUID = "bc50d971-d4c9-42c4-82cb-343fb7f37896"

DEPENDS:append = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'optee-ftpm', 'optee-ftpm', '' , d)} \
"

python() {
    if bb.utils.contains('MACHINE_FEATURES', 'optee-ftpm', True, False, d):
        d.appendVar('EARLY_TA_PATHS', ' ${STAGING_DIR_TARGET}/${base_libdir}/optee_armtz/${FTPM_UUID}.stripped.elf')
        d.appendVar('EXTRA_OEMAKE', ' CFG_CORE_HEAP_SIZE=131072')
}
