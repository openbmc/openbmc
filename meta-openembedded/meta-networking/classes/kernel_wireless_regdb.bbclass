# Linux kernels before v4.15, allowed to be compiled with static
# regulatory database if it was put under net/wireless/db.txt.
#
# This class copies the regulatory plaintext database to kernel sources before
# compiling.
#
# Usage:
# 1. The class should be inherited by kernel recipe (e.g. in
#    linux-yocto_%.bbappend).
# 2. For Linux kernels up to v4.14, build kernel with CONFIG_EXPERT and
#    CONFIG_CFG80211_INTERNAL_REGDB.

DEPENDS += "wireless-regdb-native"

SRCTREECOVEREDTASKS += "do_kernel_add_regdb"
do_kernel_add_regdb() {
    cp ${STAGING_LIBDIR_NATIVE}/crda/db.txt ${S}/net/wireless/db.txt
}
do_kernel_add_regdb[dirs] = "${S}"
addtask kernel_add_regdb before do_compile after do_configure
