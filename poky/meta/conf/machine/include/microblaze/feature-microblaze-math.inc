# This include describes the math features (integer and floating point) that
# are available for the MicroBlaze ISA

TUNEVALID[multiply-low] = "Hardware multipler with 32 bit result"
TUNEVALID[multiply-high] = "Hardware multipler with 64 bit result"
TUNEVALID[divide-hard] = "Hardware divider"
TUNEVALID[fpu-soft] = "Software FPU"
TUNEVALID[fpu-hard] = "Hardware FPU in basic mode"
TUNEVALID[fpu-hard-extended] = "Hardware FPU in extended mode (conversion and square root instructions)"

TUNECONFLICTS[fpu-soft] = "fpu-hard fpu-hard-extended"
TUNECONFLICTS[fpu-hard] = "fpu-soft"
TUNECONFLICTS[fpu-hard-extended] = "fpu-soft"

# Compiler args
TUNE_CCARGS .= "${@bb.utils.contains_any('TUNE_FEATURES', ['multiply-low', 'multiply-high'], ' -mno-xl-soft-mul', ' -mxl-soft-mul', d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'multiply-high', ' -mxl-multiply-high', '', d)}"

TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'divide-hard', ' -mno-xl-soft-div', ' -mxl-soft-div', d)}"

TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-soft', '', '', d)}"
TUNE_CCARGS .= "${@bb.utils.contains_any('TUNE_FEATURES', ['fpu-hard', 'fpu-hard-extended'], ' -mhard-float', '', d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-hard-extended', ' -mxl-float-convert -mxl-float-sqrt', '', d)}"

# Set target fpu (bitbake known target) to soft or hard (basic or extended)
TARGET_FPU = "${@bb.utils.contains_any('TUNE_FEATURES', 'fpu-hard fpu-hard-extended', 'fpu-hard', 'fpu-soft', d)}"

# Package Architecture formatting
MBPKGARCH_MATH = ""
MBPKGARCH_MATH .= "${@bb.utils.contains('TUNE_FEATURES', 'multiply-high', '-mh', bb.utils.contains('TUNE_FEATURES', 'multiply-low', '-ml', '', d), d)}"
MBPKGARCH_MATH .= "${@bb.utils.contains('TUNE_FEATURES', 'divide-hard', '-div', '', d)}"
MBPKGARCH_MATH .= "${@bb.utils.contains('TUNE_FEATURES', 'fpu-hard-extended', '-fe', bb.utils.contains('TUNE_FEATURES', 'fpu-hard', '-fb', '', d), d)}"

