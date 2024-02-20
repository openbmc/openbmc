#
# Tune Settings for neoverse-512tvb
#
DEFAULTTUNE ?= "neoverse512tvb"

TUNEVALID[neoverse512tvb] = "Enable neoverse-512tvb specific processor optimizations"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'neoverse512tvb', ' -mcpu=neoverse-512tvb', '', d)}"

require conf/machine/include/arm/arch-armv8-4a.inc

AVAILTUNES                                              += "neoverse512tvb"
ARMPKGARCH:tune-neoverse512tvb                           = "neoverse512tvb"
TUNE_FEATURES:tune-neoverse512tvb                        = "aarch64 crypto neoverse512tvb"
PACKAGE_EXTRA_ARCHS:tune-neoverse512tvb                  = "${PACKAGE_EXTRA_ARCHS:tune-armv8-4a-crypto} neoverse512tvb"
BASE_LIB:tune-neoverse512tvb                             = "lib64"
