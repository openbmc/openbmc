# MicroBlaze architecture tune feature configuration

# Architecture feature and override
TUNEVALID[microblaze] = "MicroBlaze"
MACHINEOVERRIDES =. "${@bb.utils.contains("TUNE_FEATURES", "microblaze", "microblaze:", "", d)}"

# Endian
TUNEVALID[bigendian] = "Use Microblaze Big Endian"
TUNECONFLICTS[bigendian] += "v10.0"

MBPKGARCH_ENDIAN = "${@bb.utils.contains("TUNE_FEATURES", "bigendian", "eb", "el", d)}"

TUNE_CCARGS += "${@bb.utils.contains("TUNE_FEATURES", "bigendian", "-mbig-endian", "-mlittle-endian", d)}"

# General features
TUNEVALID[barrel-shift] = "Enable Hardware Barrel Shifter"
TUNEVALID[pattern-compare] = "Enable Pattern Compare Instructions"
TUNEVALID[reorder] = "Enable Reorder Instructions"
TUNECONFLICTS[reorder] += "v8.00 v8.10 v8.20"

# Core configuration tune optimizations
TUNEVALID[frequency-optimized] = "Enabling tuning for frequency optimized core (AREA_OPTIMIZED_2)"
TUNECONFLICTS[frequency-optimized] += "v8.00 v8.10 v8.20 v8.30 v8.40 v8.50 v9.0 v9.1 v9.2 v9.3 v9.4 v9.5 v9.6"

# Feature compiler args
TUNE_CCARGS += "${@bb.utils.contains("TUNE_FEATURES", "barrel-shift", "-mxl-barrel-shift", "-mno-xl-barrel-shift", d)}"
TUNE_CCARGS += "${@bb.utils.contains("TUNE_FEATURES", "pattern-compare", "-mxl-pattern-compare", "-mno-xl-pattern-compare", d)}"
TUNE_CCARGS += "${@bb.utils.contains("TUNE_FEATURES", "frequency-optimized", "-mxl-frequency", "", d)}"

# Disable reorder for v8.30 if pattern-compare is not enabled
TUNE_CCARGS += "${@bb.utils.contains("TUNE_FEATURES", "reorder", \
        bb.utils.contains("TUNE_FEATURES", "v8.30", \
            bb.utils.contains("TUNE_FEATURES", "pattern-compare", \
                "-mxl-reorder", "-mno-xl-reorder", d), \
            "-mxl-reorder", d), "-mno-xl-reorder", d)}"

# Feature package architecture formatting
MBPKGARCH_TUNE = ""
MBPKGARCH_TUNE .= "${@bb.utils.contains("TUNE_FEATURES", "barrel-shift", "-bs", "", d)}"
MBPKGARCH_TUNE .= "${@bb.utils.contains("TUNE_FEATURES", "pattern-compare", "-cmp", "", d)}"
MBPKGARCH_TUNE .= "${@bb.utils.contains("TUNE_FEATURES", "reorder", "-re", "", d)}"
MBPKGARCH_TUNE .= "${@bb.utils.contains("TUNE_FEATURES", "frequency-optimized", "-fo", "", d)}"

# Additional features
require conf/machine/include/microblaze/feature-microblaze-versions.inc
require conf/machine/include/microblaze/feature-microblaze-math.inc

# Architecture name, either 'microblazeeb' or 'microblazeel' depending on endianess
TUNE_ARCH = "microblaze${@bb.utils.contains("TUNE_FEATURES", "bigendian", "eb", "el", d)}"

# Package Architecture formatting
TUNE_PKGARCH = "microblaze${MBPKGARCH_ENDIAN}${MBPKGARCH_VERSION}${MBPKGARCH_TUNE}${MBPKGARCH_MATH}"

