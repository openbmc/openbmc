# Settings for the GCC(1) cpu-type "quark":
#
#
#
DEFAULTTUNE ?= "i586-nlp-32"

# Include the previous tune to pull in PACKAGE_EXTRA_ARCHS
require conf/machine/include/x86/arch-x86.inc

# x86 with no lock prefix
TUNEVALID[i586-nlp] = "IA32 with Lock Prefix omitted"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'i586-nlp', ' -march=i586 -Wa,-momit-lock-prefix=yes', '', d)}"

# Quark tune feature
AVAILTUNES = "i586-nlp-32"
TUNE_FEATURES_tune-i586-nlp-32 = "${TUNE_FEATURES_tune-x86} i586-nlp"
BASE_LIB_tune-i586-nlp-32 = "lib"
TUNE_PKGARCH_tune-i586-nlp-32 = "i586-nlp-32"
PACKAGE_EXTRA_ARCHS_tune-i586-nlp-32 = "i586-nlp-32"
