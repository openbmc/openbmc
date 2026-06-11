require recipes-devtools/gcc/gcc-${PV}.inc
require libgcc-initial.inc

# Building with thumb enabled on armv6t fails
ARM_INSTRUCTION_SET:armv6 = "arm"
