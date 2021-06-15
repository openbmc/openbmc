# MicroBlaze does not support LTTng UST
LTTNGUST_microblaze = ""

# Add MicroBlaze patches
FILESEXTRAPATHS_append := ":${THISDIR}/gdb"

SRC_URI_append_microblaze = " \
     file://0001-Add-wdc.ext.clear-and-wdc.ext.flush-insns.patch \
     file://0003-Disable-the-warning-message-for-eh_frame_hdr.patch \
     file://0004-LOCAL-Fix-relaxation-of-assembler-resolved-reference.patch \
     file://0005-upstream-change-to-garbage-collection-sweep-causes-m.patch \
     file://0006-Fix-bug-in-TLSTPREL-Relocation.patch \
     file://0007-Added-Address-extension-instructions.patch \
     file://0008-fixing-the-MAX_OPCODES-to-correct-value.patch \
     file://0009-Add-new-bit-field-instructions.patch \
     file://0010-fixing-the-imm-bug.patch \
     file://0014-intial-commit-of-MB-64-bit.patch \
     file://0015-MB-X-initial-commit.patch \
     file://0016-Patch-Microblaze-negl-instruction-is-overriding-rsub.patch \
     file://0017-Added-relocations-for-MB-X.patch \
     file://0018-Fixed-MB-x-relocation-issues.patch \
     file://0019-Fixing-the-branch-related-issues.patch \
     file://0020-Fixed-address-computation-issues-with-64bit-address.patch \
     file://0021-Adding-new-relocation-to-support-64bit-rodata.patch \
     file://0022-fixing-the-.bss-relocation-issue.patch \
     file://0023-Fixed-the-bug-in-the-R_MICROBLAZE_64_NONE-relocation.patch \
     file://0025-fixing-the-long-long-long-mingw-toolchain-issue.patch \
     file://0026-Added-support-to-new-arithmetic-single-register-inst.patch \
     file://0027-Patch-MicroBlaze-double-imml-generation-for-64-bit-v.patch \
     file://0032-ld-emulparams-elf64microblaze-Fix-emulation-generati.patch \
     file://0033-Add-initial-port-of-linux-gdbserver-add-gdb_proc_ser.patch \
     file://0034-Initial-port-of-core-reading-support-Added-support-f.patch \
     file://0035-Fix-debug-message-when-register-is-unavailable.patch \
     file://0036-revert-master-rebase-changes-to-gdbserver.patch \
     file://0037-revert-master-rebase-changes-to-gdbserver-previous-c.patch \
     file://0038-microblaze-Add-build_gdbserver-yes-to-top-level-conf.patch \
     file://0039-Initial-support-for-native-gdb.patch \
     file://0040-Fixing-the-issues-related-to-GDB-7.12-added-all-the-.patch \
     file://0041-Adding-64-bit-MB-support-Added-new-architecture-to-M.patch \
     file://0042-porting-GDB-for-linux.patch \
     file://0043-Binutils-security-check-is-causing-build-error-for-w.patch \
     file://0044-Correcting-the-register-names-from-slr-shr-to-rslr-r.patch \
     file://0045-Removing-the-header-gdb_assert.h-from-MB-target-file.patch \
     file://0046-bfd-cpu-microblaze.c-Enhance-disassembler.patch \
     file://0047-bfd-elf64-microblaze.c-Fix-build-failures.patch \
     file://0048-bfd-elf-microblaze.c-Remove-obsolete-entries.patch \
     file://0049-bfd-elf64-microblaze.c-Resolve-various-compiler-warn.patch \
     file://0050-opcodes-microblaze-dis.c-Fix-compile-warnings.patch \
     file://0051-gdb-microblaze-tdep.c-Remove-unused-functions.patch \
     file://0052-sim-Allow-microblaze-architecture.patch \
     file://0053-gdb-Fix-microblaze-target-compilation.patch \
     "

#
##     file://0048-bfd-gas-Use-standard-method-to-set-the-machine-arch.patch \
##     file://0052-opcodes-microblaze-opc.h-Expand-the-size-to-int-to-d.patch \
##     file://0053-opcodes-microblaze-opc.h-MIN_IMML-is-too-large.patch \
#
