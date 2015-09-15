TUNEVALID[vfp] = "Enable Vector Floating Point (vfp) unit."
ARMPKGSFX_FPU .= "${@bb.utils.contains("TUNE_FEATURES", "vfp", "-vfp", "" ,d)}"

TUNEVALID[vfpv3d16] = "Enable Vector Floating Point Version 3 with 16 registers (vfpv3-d16) unit."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv3d16", " -mfpu=vfpv3-d16", "", d)}"
ARMPKGSFX_FPU .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv3d16", "-vfpv3d16", "" ,d)}"

TUNEVALID[vfpv3] = "Enable Vector Floating Point Version 3 with 32 registers (vfpv3) unit."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv3", " -mfpu=vfpv3", "", d)}"
ARMPKGSFX_FPU .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv3", "-vfpv3", "" ,d)}"

TUNEVALID[vfpv4] = "Enable Vector Floating Point Version 4 (vfpv4) unit."
ARMPKGSFX_FPU .= "${@bb.utils.contains("TUNE_FEATURES", "vfpv4", "-vfpv4", "" ,d)}"

TUNEVALID[callconvention-hard] = "Enable EABI hard float call convention, requires VFP."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "vfp", bb.utils.contains("TUNE_FEATURES", "callconvention-hard", " -mfloat-abi=hard", " -mfloat-abi=softfp", d), "" ,d)}"
ARMPKGSFX_EABI .= "${@bb.utils.contains("TUNE_FEATURES", [ "callconvention-hard", "vfp" ], "hf", "", d)}"
