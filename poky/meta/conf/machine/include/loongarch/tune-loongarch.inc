require conf/machine/include/loongarch/arch-loongarch.inc

TUNEVALID[loongarch64] = "Enable 64-bit LoongArch optimizations"

TUNEVALID[littleendian] = "Little endian mode"

AVAILTUNES += "loongarch64"

# Default
TUNE_FEATURES:tune-loongarch64 = "loongarch64"
TUNE_ARCH:tune-loongarch64 = "loongarch64"
TUNE_PKGARCH:tune-loongarch64 = "loongarch64"
PACKAGE_EXTRA_ARCHS:tune-loongarch64 = "loongarch64"
