require conf/machine/include/riscv/arch-riscv.inc

TUNEVALID[riscv64] = "Enable 64-bit RISC-V optimizations"
TUNEVALID[riscv32] = "Enable 32-bit RISC-V optimizations"

TUNEVALID[littleendian] = "Little endian mode"

AVAILTUNES += "riscv64 riscv32"

TUNE_FEATURES_tune-riscv64 = "riscv64 littleendian"
TUNE_ARCH_tune-riscv64 = "riscv64"
TUNE_PKGARCH_tune-riscv64 = "riscv64"
PACKAGE_EXTRA_ARCHS_tune-riscv64 = "riscv64"

TUNE_FEATURES_tune-riscv32 = "riscv32 littleendian"
TUNE_ARCH_tune-riscv32 = "riscv32"
TUNE_PKGARCH_tune-riscv32 = "riscv32"
PACKAGE_EXTRA_ARCHS_tune-riscv32 = "riscv32"

