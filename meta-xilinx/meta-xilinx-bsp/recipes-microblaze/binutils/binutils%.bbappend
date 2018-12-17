FILESEXTRAPATHS_append_microblaze := "${THISDIR}/binutils-2.31:"
SRC_URI_append_microblaze = " \
		file://0001-MicroBlaze-Add-wdc.ext.clear-and-wdc.ext.flush-insns.patch \
		file://0002-MicroBlaze-add-mlittle-endian-and-mbig-endian-flags.patch \
		file://0003-Disable-the-warning-message-for-eh_frame_hdr.patch \
		file://0004-Fix-relaxation-of-assembler-resolved-references.patch \
		file://0005-Fixup-MicroBlaze-debug_loc-sections-after-linker-rel.patch \
		file://0006-Fix-bug-in-MicroBlaze-TLSTPREL-Relocation.patch \
		file://0007-Add-MicroBlaze-address-extension-instructions.patch \
		file://0008-Add-new-MicroBlaze-bit-field-instructions.patch \
		file://0009-Fixing-MicroBlaze-IMM-bug.patch \
		file://0010-Fixed-bug-in-GCC-so-that-it-will-support-.long-0U-an.patch \
		file://0011-Fixing-MicroBlaze-constant-range-check-issue.patch \
		file://0012-MicroBlaze-fix-mask-for-barrel-shift-instructions.patch \
		"
