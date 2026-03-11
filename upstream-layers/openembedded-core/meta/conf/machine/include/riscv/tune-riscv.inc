require conf/machine/include/riscv/arch-riscv.inc

DEFAULTTUNE ?= "riscv64"

AVAILTUNES += "riscv64 riscv32 riscv64nc riscv64nf riscv32nf"

# Default
TUNE_FEATURES:tune-riscv64 := "${@oe.tune.riscv_isa_to_tune("rv64gc")}"
PACKAGE_EXTRA_ARCHS:tune-riscv64 = "${TUNE_RISCV_PKGARCH}"

TUNE_FEATURES:tune-riscv32 := "${@oe.tune.riscv_isa_to_tune("rv32gc")}"
PACKAGE_EXTRA_ARCHS:tune-riscv32 = "${TUNE_RISCV_PKGARCH}"

# No float
TUNE_FEATURES:tune-riscv64nf := "${@oe.tune.riscv_isa_to_tune("rv64imac_zicsr_zifencei")}"
PACKAGE_EXTRA_ARCHS:tune-riscv64nf = "${TUNE_RISCV_PKGARCH}"

TUNE_FEATURES:tune-riscv32nf := "${@oe.tune.riscv_isa_to_tune("rv32imac_zicsr_zifencei")}"
PACKAGE_EXTRA_ARCHS:tune-riscv32nf = "${TUNE_RISCV_PKGARCH}"

# no compressed
TUNE_FEATURES:tune-riscv64nc := "${@oe.tune.riscv_isa_to_tune("rv64imafd_zicsr_zifencei")}"
PACKAGE_EXTRA_ARCHS:tune-riscv64nc = "${TUNE_RISCV_PKGARCH}"
