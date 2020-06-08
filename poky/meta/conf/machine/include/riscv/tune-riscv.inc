require conf/machine/include/riscv/arch-riscv.inc

TUNEVALID[riscv64] = "Enable 64-bit RISC-V optimizations"
TUNEVALID[riscv32] = "Enable 32-bit RISC-V optimizations"

TUNEVALID[riscv64nf] = "Enable 64-bit RISC-V optimizations no floating point"
TUNEVALID[riscv32nf] = "Enable 32-bit RISC-V optimizations no floating point"

TUNEVALID[bigendian] = "Big endian mode"

AVAILTUNES += "riscv64 riscv32 riscv64nf riscv32nf"

# Default
TUNE_FEATURES_tune-riscv64 = "riscv64"
TUNE_ARCH_tune-riscv64 = "riscv64"
TUNE_PKGARCH_tune-riscv64 = "riscv64"
PACKAGE_EXTRA_ARCHS_tune-riscv64 = "riscv64"

TUNE_FEATURES_tune-riscv32 = "riscv32"
TUNE_ARCH_tune-riscv32 = "riscv32"
TUNE_PKGARCH_tune-riscv32 = "riscv32"
PACKAGE_EXTRA_ARCHS_tune-riscv32 = "riscv32"

# No float
TUNE_FEATURES_tune-riscv64nf = "${TUNE_FEATURES_tune-riscv64} riscv64nf"
TUNE_ARCH_tune-riscv64nf = "riscv64"
TUNE_PKGARCH_tune-riscv64nf = "riscv64"
PACKAGE_EXTRA_ARCHS_tune-riscv64nf = "riscv64nf"

TUNE_FEATURES_tune-riscv32nf = "${TUNE_FEATURES_tune-riscv32} riscv32nf"
TUNE_ARCH_tune-riscv32nf = "riscv32"
TUNE_PKGARCH_tune-riscv32nf = "riscv32"
PACKAGE_EXTRA_ARCHS_tune-riscv32nf = "riscv32nf"
