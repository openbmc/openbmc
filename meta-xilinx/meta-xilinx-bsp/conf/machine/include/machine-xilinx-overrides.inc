SOC_VARIANT ??= ""
MACHINEOVERRIDES =. "${@['', '${SOC_FAMILY}${SOC_VARIANT}:']['${SOC_VARIANT}' != '']}"

# Here we can extend overrides for the corresponding family and variant
def get_soc_overrides(fam, var, d):
  extender = ''
  if (fam == 'zynqmp'):
    if (var == 'eg'):
      extender = 'mali400:'
    elif (var == 'ev'):
      extender = 'mali400:vcu:'
  return extender

# Gets OVERRIDES extender depending on the board
SOC_OVERRIDES = "${@get_soc_overrides(d.getVar('SOC_FAMILY'),d.getVar('SOC_VARIANT'), d)}"

MACHINEOVERRIDES  =. "${SOC_OVERRIDES}"

PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY}"
PACKAGE_EXTRA_ARCHS_append = "${@['', ' ${SOC_FAMILY}${SOC_VARIANT}']['${SOC_VARIANT}' != '']}"

