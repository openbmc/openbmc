DEFAULTTUNE ?= "ppce300c3"

require conf/machine/include/powerpc/arch-powerpc.inc

AVAILTUNES += "ppce300c3 ppce300c3-nf"

# hard-float
TUNEVALID[ppce300c3] = "Enable ppce300c3 specific processor optimizations"
TUNE_FEATURES_tune-ppce300c3 = "${TUNE_FEATURES_tune-powerpc} ppce300c3"
TUNE_PKGARCH_tune-ppce300c3 = "ppce300c3"
PACKAGE_EXTRA_ARCHS_tune-ppce300c3 = "${PACKAGE_EXTRA_ARCHS_tune-powerpc} ppce300c3"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "ppce300c3", " -mcpu=e300c3", "", d)}"

# glibc config options to make use of e300c3 (603e) specific sqrt/sqrtf routines
GLIBC_EXTRA_OECONF += "${@bb.utils.contains("TUNE_FEATURES", "ppce300c3", "--with-cpu=e300c3", "", d)}"

# soft-float
TUNEVALID[ppce300c3-nf] = "Enable ppce300c3 specific processor optimizations (no fpu)"
TUNE_FEATURES_tune-ppce300c3-nf = "${TUNE_FEATURES_tune-powerpc-nf} ppce300c3-nf"
TUNE_PKGARCH_tune-ppce300c3-nf = "ppce300c3-nf"
PACKAGE_EXTRA_ARCHS_tune-ppce300c3-nf = "${PACKAGE_EXTRA_ARCHS_tune-powerpc-nf} ppce300c3-nf"
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "ppce300c3-nf", " -mcpu=e300c3", "", d)}"

