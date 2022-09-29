require conf/machine/include/arm/arch-armv8r.inc

TUNE_FEATURES:tune-armv8r            =+ "aarch64"
PACKAGE_EXTRA_ARCHS:tune-armv8r      =+ "aarch64"
BASE_LIB:tune-armv8r                 = "lib64"
BASE_LIB:tune-armv8r-crc             = "lib64"
BASE_LIB:tune-armv8r-crypto          = "lib64"
BASE_LIB:tune-armv8r-simd            = "lib64"
BASE_LIB:tune-armv8r-crc-simd        = "lib64"
BASE_LIB:tune-armv8r-crc-crypto-simd = "lib64"
