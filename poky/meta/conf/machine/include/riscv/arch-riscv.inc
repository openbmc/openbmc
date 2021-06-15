# RISCV Architecture definition

DEFAULTTUNE ?= "riscv64"

TUNE_ARCH = "${TUNE_ARCH_tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"
TUNE_CCARGS_append = "${@bb.utils.contains('TUNE_FEATURES', 'riscv64nf', ' -mabi=lp64', ' ', d)}"
TUNE_CCARGS_append = "${@bb.utils.contains('TUNE_FEATURES', 'riscv32nf', ' -mabi=ilp32', ' ', d)}"

# Fix: ld: unrecognized option '--hash-style=sysv'
LINKER_HASH_STYLE_libc-newlib = ""
# Fix: ld: unrecognized option '--hash-style=gnu'
LINKER_HASH_STYLE_libc-baremetal = ""
