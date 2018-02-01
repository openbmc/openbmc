# This is only for VFPv1 or VFPv2, other vfp* tunes are defined in
# feature-arm-neon.inc, because they are available only for armv7*
# and this .inc file is included from armv5

TUNEVALID[vfp] = "Enable Vector Floating Point (vfp) unit."
TUNE_CCARGS_MFPU .= "${@bb.utils.contains('TUNE_FEATURES', 'vfp', ' vfp', '', d)}"

TUNE_CCARGS  .= "${@ (' -mfpu=%s ' % d.getVar('TUNE_CCARGS_MFPU').split()[-1]) if (d.getVar('TUNE_CCARGS_MFPU') != '') else ''}"
ARMPKGSFX_FPU = "${@ ('-%s'        % d.getVar('TUNE_CCARGS_MFPU').split()[-1].replace('vfpv3-d16', 'vfpv3d16')) if (d.getVar('TUNE_CCARGS_MFPU') != '') else ''}"

TUNEVALID[callconvention-hard] = "Enable EABI hard float call convention, requires VFP."
TUNE_CCARGS_MFLOAT = "${@ bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hard', 'softfp', d) if (d.getVar('TUNE_CCARGS_MFPU') != '') else '' }"
TUNE_CCARGS   .= "${@ ' -mfloat-abi=${TUNE_CCARGS_MFLOAT}' if (d.getVar('TUNE_CCARGS_MFLOAT') != '')     else ''}"
ARMPKGSFX_EABI = "${@ 'hf'                                 if (d.getVar('TUNE_CCARGS_MFLOAT') == 'hard') else ''}"
