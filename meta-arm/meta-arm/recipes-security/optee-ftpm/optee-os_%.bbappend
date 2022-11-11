FTPM_UUID="bc50d971-d4c9-42c4-82cb-343fb7f37896"

DEPENDS:append = "\
                  ${@bb.utils.contains('MACHINE_FEATURES', \
                 'optee-ftpm', \
                 'optee-ftpm', \
                 '' , \
                 d)}"

EXTRA_OEMAKE:append = "\
                       ${@bb.utils.contains('MACHINE_FEATURES', \
                      'optee-ftpm', \
                      'CFG_EARLY_TA=y EARLY_TA_PATHS="${STAGING_DIR_TARGET}/lib/optee_armtz/${FTPM_UUID}.stripped.elf"', \
                      '', \
                      d)} "
