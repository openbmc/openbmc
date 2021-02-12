COMPATIBLE_HOST = ".*-elf"
COMPATIBLE_HOST_arm = "[^-]*-[^-]*-eabi"

EXTRA_OECONF_append_xilinx-standalone = " \
	--enable-newlib-io-c99-formats \
	--enable-newlib-io-long-long \
	--enable-newlib-io-float \
	--enable-newlib-io-long-double \
"

# Avoid trimmping CCARGS from CC by newlib configure
do_configure_prepend_xilinx-standalone(){
    export CC_FOR_TARGET="${CC}"
}

# Fix for multilib newlib installations
do_install_prepend_xilinx-standalone() {
        mkdir -p $(dirname ${D}${libdir})
        mkdir -p $(dirname ${D}${includedir})
}
