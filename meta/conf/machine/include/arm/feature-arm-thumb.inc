# The instruction set the compiler should use when generating application
# code. The kernel is always compiled with arm code at present. arm code
# is the original 32 bit ARM instruction set, thumb code is the 16 bit
# encoded RISC sub-set. Thumb code is smaller (maybe 70% of the ARM size)
# but requires more instructions (140% for 70% smaller code) so may be
# slower.
TUNEVALID[thumb] = "Use thumb instructions instead of ARM"
ARM_THUMB_OPT = "${@['arm', 'thumb'][d.getVar('ARM_INSTRUCTION_SET', True) == 'thumb']}"
ARM_THUMB_SUFFIX .= "${@bb.utils.contains('TUNE_FEATURES', 'armv4',  't',  '', d)}"
ARM_THUMB_SUFFIX .= "${@bb.utils.contains('TUNE_FEATURES', 'armv5',  't',  '', d)}"
ARM_THUMB_SUFFIX .= "${@bb.utils.contains('TUNE_FEATURES', 'armv6',  't',  '', d)}"
ARM_THUMB_SUFFIX .= "${@bb.utils.contains('TUNE_FEATURES', 'armv7a', 't2', '', d)}"
ARM_THUMB_SUFFIX .= "${@bb.utils.contains('TUNE_FEATURES', 'armv7r', 't2', '', d)}"
ARM_THUMB_SUFFIX .= "${@bb.utils.contains('TUNE_FEATURES', 'armv7m', 't2', '', d)}"

# If the device supports ARM, then respect ARM_THUMB_OPT (which can be "arm" or "thumb")
# If the defice doesn't support ARM, then always set "thumb" even when
# some recipe explicitly sets ARM_INSTRUCTION_SET to "arm"
ARM_M_OPT = "${@bb.utils.contains('TUNE_FEATURES', 'arm', '${ARM_THUMB_OPT}', 'thumb', d)}"
python () {
    if bb.utils.contains('TUNE_FEATURES', 'thumb', 'False', 'True', d):
        return
    selected = d.getVar('ARM_INSTRUCTION_SET', True)
    if selected == None:
        return
    used = d.getVar('ARM_M_OPT', True)
    if selected != used:
        pn = d.getVar('PN', True)
        bb.warn("Recipe '%s' selects ARM_INSTRUCTION_SET to be '%s', but tune configuration overrides it to '%s'" % (pn, selected, used))
}

TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', ' -m${ARM_M_OPT}', '', d)}"
OVERRIDES .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', ':thumb', '', d)}"

# Add suffix from ARM_THUMB_SUFFIX only if after all this we still set ARM_M_OPT to thumb
ARMPKGSFX_THUMB .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', '${ARM_THUMB_SUFFIX}', '', d) if d.getVar('ARM_M_OPT', True) == 'thumb' else ''}"

# Whether to compile with code to allow interworking between the two
# instruction sets. This allows thumb code to be executed on a primarily
# arm system and vice versa. It is strongly recommended that DISTROs not
# turn this off - the actual cost is very small.
TUNEVALID[no-thumb-interwork] = "Disable mixing of thumb and ARM functions"
THUMB_TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'no-thumb-interwork', ' -mno-thumb-interwork', ' -mthumb-interwork', d)}"
THUMB_OVERRIDES .= "${@bb.utils.contains('TUNE_FEATURES', 'no-thumb-interwork', ':thumb-interwork', '', d)}"
TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', ' ${THUMB_TUNE_CCARGS}', '', d)}"
OVERRIDES .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', '${THUMB_OVERRIDES}', '', d)}"

# what about armv7m devices which don't support -marm (e.g. Cortex-M3)?
TARGET_CC_KERNEL_ARCH += "${@bb.utils.contains('TUNE_FEATURES', 'thumb', '-mno-thumb-interwork -marm', '', d)}"
