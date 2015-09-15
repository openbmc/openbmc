TUNEVALID[neon] = "Enable Neon SIMD accelerator unit."
TUNE_CCARGS .= "${@bb.utils.contains("TUNE_FEATURES", "neon", bb.utils.contains("TUNE_FEATURES", "vfpv4", " -mfpu=neon-vfpv4", " -mfpu=neon", d), "" , d)}"
ARMPKGSFX_FPU .= "${@bb.utils.contains("TUNE_FEATURES", "neon", "-neon", "", d)}"
