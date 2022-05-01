# RISCV Architecture definition

DEFAULTTUNE ?= "riscv64"

TUNE_ARCH = "${TUNE_ARCH:tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH:tune-${DEFAULTTUNE}}"
TUNE_CCARGS:append = "${@bb.utils.contains('TUNE_FEATURES', 'riscv64nf', ' -mabi=lp64', ' ', d)}"
TUNE_CCARGS:append = "${@bb.utils.contains('TUNE_FEATURES', 'riscv32nf', ' -mabi=ilp32', ' ', d)}"

TUNE_CCARGS:append = "${@bb.utils.contains('TUNE_FEATURES', 'riscv64nc', ' -march=rv64imafd', ' ', d)}"

# Fix: ld: unrecognized option '--hash-style=sysv'
LINKER_HASH_STYLE:libc-newlib = ""
# Fix: ld: unrecognized option '--hash-style=gnu'
LINKER_HASH_STYLE:libc-baremetal = ""
