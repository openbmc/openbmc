DEFAULTTUNE ?= "i586"

require conf/machine/include/x86/arch-x86.inc

# Extra tune features
TUNEVALID[i586] = "Enable i586 specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'i586', ' -march=i586', '', d)}"

# Extra tune selections
AVAILTUNES += "i586"
TUNE_FEATURES:tune-i586 = "${TUNE_FEATURES:tune-x86} i586"
BASE_LIB:tune-i586 = "lib"
TUNE_PKGARCH:tune-i586 = "i586"
PACKAGE_EXTRA_ARCHS:tune-i586 = "${PACKAGE_EXTRA_ARCHS:tune-x86} i586"
