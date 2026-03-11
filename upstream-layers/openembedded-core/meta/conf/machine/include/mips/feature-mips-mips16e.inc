TUNEVALID[mips16e] = "Build target packages with MIPS16e ASE instructions"
MIPS_MIPS16E_OPT = "${@['mno-mips16', 'mips16'][d.getVar('MIPS_INSTRUCTION_SET') == 'mips16e']}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mips16e', ' -${MIPS_MIPS16E_OPT}', '', d)}"

MIPSPKGSFX_MIPS16E .= "${@bb.utils.contains('TUNE_FEATURES', 'mips16e', '-m16', '', d) if d.getVar('MIPS_MIPS16E_OPT') == 'mips16' else ''}"

# Whether to compile with code to allow interworking between the two
# instruction sets. This allows mips16e code to be executed on a primarily
# mips32/64 system and vice versa. It is strongly recommended that DISTROs not
# turn this off - the actual cost is very small.
TUNEVALID[no-interlink-compressed] = "Disable mixing of standard and MIPS16e code"
MIPS16_TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'no-interlink-compressed', ' -mno-interlink-compressed', ' -minterlink-compressed', d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'mips16e', ' ${MIPS16_TUNE_CCARGS}', '', d)}"
MACHINEOVERRIDES =. "${@bb.utils.contains('TUNE_FEATURES', 'mips16e', 'mips16e:', '', d)}"

# show status (if compiling in MIPS16e mode)
BUILDCFG_VARS += "${@['', 'MIPS_INSTRUCTION_SET'][d.getVar('MIPS_INSTRUCTION_SET') == 'mips16e']}"
