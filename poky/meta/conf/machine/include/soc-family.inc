# Add SOC_FAMILY to machine overrides so we get access to e.g. 'omap3' and 'ti335x'
SOC_FAMILY ??= ""
MACHINEOVERRIDES =. "${@['', '${SOC_FAMILY}:']['${SOC_FAMILY}' != '']}"
