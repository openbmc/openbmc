COMPATIBLE_HOST = ".*-elf"
COMPATIBLE_HOST_arm = "[^-]*-[^-]*-eabi"

# When building multiple, we need to depend on the multilib newlib
DEPENDS_append_xilinx-standalone = " ${MLPREFIX}newlib"

EXTRA_OECONF_append_xilinx-standalone = " \
	--enable-newlib-io-c99-formats \
	--enable-newlib-io-long-long \
	--enable-newlib-io-float \
	--enable-newlib-io-long-double \
"
