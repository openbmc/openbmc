# Avoid installing all of the alternative toolchains
# due to multilib enabled in the primary toolchain.

# Create the links to the multilib toolchain components
GNUTCLINKS = "gnu-toolchain-canadian-${TARGET_SYS}"

# Without the := the eval during do_package is occasionally missing multilibs
RDEPENDS_${PN}_xilinx-standalone := " \
    ${@all_multilib_tune_values(d, 'GNUTCLINKS')} \
    ${BINUTILS} \
    ${GCC} \
    ${GDB} \
    meta-environment-${MACHINE} \
"
