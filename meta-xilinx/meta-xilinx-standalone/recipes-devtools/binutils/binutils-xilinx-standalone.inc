LDGOLD_xilinx-standalone = ""
LDGOLD_ALTS_xilinx-standalone = ""

EXTRA_OECONF_append_xilinx-standalone = " \
	--disable-gprof \
	--disable-shared \
	--enable-lto \
	--enable-static \
	--enable-multilib \
"

# CortexR5 and 32-bit arm are both "arm"
EXTRA_OECONF_append_xilinx-standalone_arm = " \
	--enable-interwork \
"

EXTRA_OECONF_append_xilinx-standalone_microblaze = " \
	--disable-initfini-array \
"
