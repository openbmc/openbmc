# RISCV Architecture definition

DEFAULTTUNE ?= "riscv64"

TUNE_ARCH = "${TUNE_ARCH_tune-${DEFAULTTUNE}}"
TUNE_PKGARCH = "${TUNE_PKGARCH_tune-${DEFAULTTUNE}}"
TUNE_CCARGS .= ""

# QEMU usermode fails with invalid instruction error (For riscv32)
MACHINE_FEATURES_BACKFILL_CONSIDERED_append = "${@bb.utils.contains('TUNE_FEATURES', 'riscv32', ' qemu-usermode', '', d)}"
