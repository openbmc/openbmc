FILESEXTRAPATHS_append := ":${THISDIR}/binutils"

SRC_URI_append = " \
     file://0001-sim-Allow-microblaze-architecture.patch \
     file://0002-Add-wdc.ext.clear-and-wdc.ext.flush-insns.patch \
     file://0003-Add-mlittle-endian-and-mbig-endian-flags.patch \
     file://0004-Disable-the-warning-message-for-eh_frame_hdr.patch \
     file://0005-Fix-relaxation-of-assembler-resolved-references.patch \
     file://0006-microblaze-Fixup-debug_loc-sections-after-linker-rel.patch \
     file://0007-upstream-change-to-garbage-collection-sweep-causes-m.patch \
     file://0008-Fix-bug-in-TLSTPREL-Relocation.patch \
     file://0009-Added-Address-extension-instructions.patch \
     file://0010-Add-new-bit-field-instructions.patch \
     file://0011-fixing-the-imm-bug.patch \
     file://0012-Patch-Microblaze-fixed-bug-in-GCC-so-that-It-will-su.patch \
     file://0013-fixing-the-constant-range-check-issue.patch \
     file://0014-Patch-Microblaze-Compiler-will-give-error-messages-i.patch \
     file://0015-intial-commit-of-MB-64-bit.patch \
     file://0016-MB-X-initial-commit.patch \
     file://0017-Patch-Microblaze-negl-instruction-is-overriding-rsub.patch \
     file://0018-Added-relocations-for-MB-X.patch \
     file://0019-Update-MB-x.patch \
     file://0020-Various-fixes.patch \
     file://0021-Adding-new-relocation-to-support-64bit-rodata.patch \
     file://0022-fixing-the-.bss-relocation-issue.patch \
     file://0023-Fixed-the-bug-in-the-R_MICROBLAZE_64_NONE-relocation.patch \
     file://0024-Revert-ld-Remove-unused-expression-state.patch \
     file://0025-Patch-Microblaze-Binutils-security-check-is-causing-.patch \
     file://0026-fixing-the-long-long-long-mingw-toolchain-issue.patch \
     file://0027-Added-support-to-new-arithmetic-single-register-inst.patch \
     file://0028-Patch-MicroBlaze-double-imml-generation-for-64-bit-v.patch \
     file://0029-Fixed-bug-in-generation-of-IMML-instruction-for-the.patch \
     file://0030-Patch-MicroBlaze-m64-Update-imml-instructions-for-Ty.patch \
     file://0031-ldlang.c-Workaround-for-improper-address-mapping-cau.patch \
     file://0032-gas-revert-moving-of-md_pseudo_table-from-const.patch \
     file://0033-Fix-various-compile-warnings.patch \
     file://0034-Add-initial-port-of-linux-gdbserver.patch \
     file://0035-Initial-port-of-core-reading-support.patch \
     file://0036-Fix-debug-message-when-register-is-unavailable.patch \
     file://0037-microblaze-Add-build_gdbserver-yes-to-top-level-conf.patch \
     file://0038-Initial-support-for-native-gdb.patch \
     file://0039-Fixing-the-issues-related-to-GDB-7.12.patch \
     file://0040-Patch-microblaze-Adding-64-bit-MB-support.patch \
     "
