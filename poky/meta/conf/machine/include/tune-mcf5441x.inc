DEFAULTTUNE ?= "mcf5441x"

require conf/machine/include/m68k/arch-m68k.inc

TUNEVALID[mcf5441x] = "Enable coldfire specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mcf5441x', ' -mcpu=54418', '', d)}"

AVAILTUNES += "mcf5441x"
TUNE_FEATURES_tune-mcf5441x = "mcf5441x"
TUNE_ARCH_tune-mcf5441x = "mcf5441x"
TUNE_PKGARCH_tune-mcf5441x = "mcf5441x"
PACKAGE_EXTRA_ARCHS_tune-mcf5441x = "m68k mcf5441x"

