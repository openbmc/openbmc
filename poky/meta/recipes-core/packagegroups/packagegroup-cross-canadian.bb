SUMMARY = "Host SDK package for cross canadian toolchain"
PN = "packagegroup-cross-canadian-${MACHINE}"

inherit cross-canadian packagegroup

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

# Use indirection to stop these being expanded prematurely
BINUTILS = "binutils-cross-canadian-${TRANSLATED_TARGET_ARCH}"
GCC = "gcc-cross-canadian-${TRANSLATED_TARGET_ARCH}"
GDB = "gdb-cross-canadian-${TRANSLATED_TARGET_ARCH}"

RDEPENDS_${PN} = "\
    ${@all_multilib_tune_values(d, 'BINUTILS')} \
    ${@all_multilib_tune_values(d, 'GCC')} \
    ${@all_multilib_tune_values(d, 'GDB')} \
    meta-environment-${MACHINE} \
    "

# When TUNE_ARCH changes but MACHINE does not (for example when a machine definition is updated), 
# cross-canadian.bbclass prevents variable dependency propagation to TRANSLATED_TARGET_ARCH
# This will result in erroneous reuse of previous sstate packages. The following line
# establishes a direct dependency instead.
do_package[vardeps] += "TUNE_ARCH"
