FILESEXTRAPATHS_append_microblaze := "${THISDIR}/binutils-2.32:"
SRC_URI_append_microblaze = " \
	file://0001-Add-wdc.ext.clear-and-wdc.ext.flush-insns.patch \
	file://0002-Add-mlittle-endian-and-mbig-endian-flags.patch \
	file://0003-Disable-the-warning-message-for-eh_frame_hdr.patch \
	file://0004-Fix-relaxation-of-assembler-resolved-references.patch \
	file://0005-LOCAL-Fixup-debug_loc-sections-after-linker-relaxati.patch \
	file://0006-upstream-change-to-garbage-collection-sweep-causes-m.patch \
	file://0007-Fix-bug-in-TLSTPREL-Relocation.patch \
	file://0008-Added-Address-extension-instructions.patch \
	file://0009-fixing-the-MAX_OPCODES-to-correct-value.patch \
	file://0010-Add-new-bit-field-instructions.patch \
	file://0011-fixing-the-imm-bug.patch \
	file://0012-Patch-Microblaze-fixed-bug-in-GCC-so-that-It-will-su.patch \
	file://0013-fixing-the-constant-range-check-issue.patch \
	file://0014-Patch-Microblaze-Compiler-will-give-error-messages-i.patch \
	file://0015-intial-commit-of-MB-64-bit.patch \
	file://0016-MB-X-initial-commit.patch \
	file://0017-Patch-Microblaze-negl-instruction-is-overriding-rsub.patch \
	file://0018-Added-relocations-for-MB-X.patch \
	file://0019-Fixed-MB-x-relocation-issues.patch \
	file://0020-Fixing-the-branch-related-issues.patch \
	file://0021-Fixed-address-computation-issues-with-64bit-address.patch \
	file://0022-Adding-new-relocation-to-support-64bit-rodata.patch \
	file://0023-fixing-the-.bss-relocation-issue.patch \
	file://0024-Fixed-the-bug-in-the-R_MICROBLAZE_64_NONE-relocation.patch \
	file://0025-Patch-MicroBlaze-fixed-Build-issue-which-are-due-to-.patch \
	file://0026-Patch-Microblaze-changes-of-PR22458-failure-to-choos.patch \
	file://0029-Patch-Microblaze-Binutils-security-check-is-causing-.patch \
	file://0030-fixing-the-long-long-long-mingw-toolchain-issue.patch \
	file://0031-fixing-the-_STACK_SIZE-issue-with-the-flto-flag.patch \
	"
