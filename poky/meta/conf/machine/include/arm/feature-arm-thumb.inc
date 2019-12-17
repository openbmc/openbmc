
TUNEVALID[thumb] = "Support Thumb instructions"

# If the device supports ARM, then respect ARM_THUMB_OPT (which can be "arm" or "thumb")
# If the device doesn't support ARM, then always set "thumb" even when
# some recipe explicitly sets ARM_INSTRUCTION_SET to "arm"
ARM_THUMB_OPT = "${@['arm', 'thumb'][d.getVar('ARM_INSTRUCTION_SET') == 'thumb']}"
ARM_M_OPT = "${@bb.utils.contains('TUNE_FEATURES', 'arm', '${ARM_THUMB_OPT}', 'thumb', d)}"

python () {
    if bb.utils.contains('TUNE_FEATURES', 'thumb', False, True, d):
        return
    selected = d.getVar('ARM_INSTRUCTION_SET')
    if selected == None:
        return
    used = d.getVar('ARM_M_OPT')
    if selected != used:
        pn = d.getVar('PN')
        bb.warn("Recipe '%s' selects ARM_INSTRUCTION_SET to be '%s', but tune configuration overrides it to '%s'" % (pn, selected, used))
}

TUNE_CCARGS .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', ' -m${ARM_M_OPT}', '', d)}"

ARM_THUMB_SUFFIX = "t2"
ARM_THUMB_SUFFIX_armv4 = "t"
ARM_THUMB_SUFFIX_armv5 = "t"
ARM_THUMB_SUFFIX_armv6 = "t"

# Add suffix from ARM_THUMB_SUFFIX only if after all this we still set ARM_M_OPT to thumb
ARMPKGSFX_THUMB .= "${@bb.utils.contains('TUNE_FEATURES', 'thumb', '${ARM_THUMB_SUFFIX}', '', d) if d.getVar('ARM_M_OPT') == 'thumb' else ''}"

# what about armv7m devices which don't support -marm (e.g. Cortex-M3)?
TARGET_CC_KERNEL_ARCH += "${@bb.utils.contains('TUNE_FEATURES', 'thumb', '-mno-thumb-interwork -marm', '', d)}"
