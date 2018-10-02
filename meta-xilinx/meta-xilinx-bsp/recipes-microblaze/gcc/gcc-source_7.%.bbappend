# Add MicroBlaze Patches (only when using MicroBlaze)
FILESEXTRAPATHS_append_microblaze := "${THISDIR}/gcc-7:"
SRC_URI_append_microblaze = " \
		file://0001-Revert.patch \
		file://0002-microblaze.md-Improve-adddi3-and-subdi3-insn-definit.patch \
		file://0003-PR-target-83013.patch \
		file://0004-dejagnu-static-testing-on-qemu-suppress-warnings.patch \
		file://0005-Testsuite-explicitly-add-fivopts-for-tests-that-depe.patch \
		file://0006-Add-MicroBlaze-to-target-supports-for-atomic-builtin.patch \
		file://0007-Update-MicroBlaze-strings-test-for-new-scan-assembly.patch \
		file://0008-Allow-MicroBlaze-.weakext-pattern-in-testsuite.patch \
		file://0009-Add-MicroBlaze-to-check_profiling_available-Testsuit.patch \
		file://0010-Fix-atomic-side-effects.patch \
		file://0011-Fix-atomic-boolean-return-value.patch \
		file://0012-Fix-the-Microblaze-crash-with-msmall-divides-flag.patch \
		file://0013-Add-MicroBlaze-ashrsi_3_with_size_opt.patch \
		file://0014-Removed-MicroBlaze-moddi3-routinue.patch \
		file://0015-MicroBlaze-fixed-missing-save-of-r18-in-fast_interru.patch \
		file://0016-MicroBlaze-use-bralid-for-profiler-calls.patch \
		file://0017-Disable-fivopts-by-default-Turn-off-ivopts-by-defaul.patch \
		file://0018-Add-INIT_PRIORITY-support-Added-TARGET_ASM_CONSTRUCT.patch \
		file://0019-MicroBlaze-add-optimized-lshrsi3-When-barrel-shifter.patch \
		file://0020-Modified-MicroBlaze-trap-instruction.patch \
		file://0021-Reducing-Stack-space-for-arguments-Currently-in-Micr.patch \
		file://0022-Inline-Expansion-of-fsqrt-builtin.patch \
		file://0023-Update-MicroBlaze-ashlsi3-movsf-patterns.patch \
		file://0024-8-stage-pipeline-for-microblaze.patch \
		file://0025-MicroBlaze-correct-the-const-high-double-immediate-v.patch \
		file://0026-Fix-internal-compiler-error-with-msmall-divides.patch \
		file://0027-Fix-the-calculation-of-high-word-in-a-long-long-64-b.patch \
		file://0028-Add-new-bit-field-instructions.patch \
		file://0029-Fix-bug-in-MB-version-calculation.patch \
		file://0030-MicroBlaze-fixing-the-bug-in-the-bit-field-instructi.patch \
		file://0031-Fixing-the-issue-with-MicroBlaze-builtin_alloc.patch \
		file://0032-MicroBlaze-remove-bitfield-instructions-macros.patch \
		file://0033-MicroBlaze-fix-signed-bit-fields-with-bit-field-inst.patch \
		"

