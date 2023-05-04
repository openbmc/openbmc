require recipes-devtools/gcc/gcc-${PV}.inc
require gcc-sanitizers.inc

# Building with thumb enabled on armv4t armv5t fails with
# sanitizer_linux.s:5749: Error: lo register required -- `ldr ip,[sp],#8'
ARM_INSTRUCTION_SET_armv4 = "arm"
ARM_INSTRUCTION_SET_armv5 = "arm"
