
# MicroBlaze does not support LTTng UST
LTTNGUST_microblaze = ""

# Add MicroBlaze Patches
FILESEXTRAPATHS_append := "${THISDIR}/files:"
SRC_URI_append = " \
		file://0001-Patch-microblaze-Add-wdc.ext.clear-and-wdc.ext.flush.patch \
		file://0003-Patch-microblaze-Initial-port-of-microblaze-core-rea.patch \
		file://0004-Patch-microblaze-Communicate-in-larger-blocks-with-t.patch \
		file://0005-Patch-microblaze-Add-mb-singlestepping.patch \
		file://0006-Patch-microblaze-Add-initial-port-of-linux-gdbserver.patch \
		file://0007-Patch-microblaze-Add-slr-and-shr-regs-and-little-end.patch \
		file://0008-Patch-microblaze-Added-Backtrace-support-to-GDB.patch \
		file://0009-Patch-microblaze-Initial-support-for-native-gdb.patch \
		file://0010-Patch-gdb-Robustify-inline-function-support.patch \
		file://0011-Patch-gdb-Fix-debug-message-when-register-is-unavail.patch \
		file://0012-LOCAL-Disable-the-warning-message-for-eh_frame_hdr.patch \
		file://0013-LOCAL-Fix-relaxation-of-assembler-resolved-reference.patch \
		file://0014-LOCAL-Fixup-debug_loc-sections-after-linker-relaxati.patch \
		file://0015-LOCAL-upstream-change-to-garbage-collection-sweep-ca.patch \
		"
