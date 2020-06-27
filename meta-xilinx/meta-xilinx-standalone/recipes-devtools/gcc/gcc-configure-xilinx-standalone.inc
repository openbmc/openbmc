# Mimic the file gcc-configure-common from the Yocto Project
LINKER_HASH_STYLE_xilinx-standalone = ""
SYMVERS_CONF_xilinx-standalone = ""

EXTRA_OECONF_append_xilinx-standalone_microblaze = " --disable-initfini_array"

EXTRA_OECONF_append_xilinx-standalone_microblaze = " --disable-__cxa_atexit"
