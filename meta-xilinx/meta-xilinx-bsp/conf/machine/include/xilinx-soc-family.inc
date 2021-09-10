require conf/machine/include/soc-family.inc

SOC_VARIANT ??= ""
MACHINEOVERRIDES =. "${@['', '${SOC_FAMILY}${SOC_VARIANT}:']['${SOC_VARIANT}' != '']}"

SOC_FAMILY_ARCH ?= "${SOC_FAMILY}"
SOC_VARIANT_ARCH ?= "${SOC_FAMILY}${SOC_VARIANT}"

PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_ARCH}"
PACKAGE_EXTRA_ARCHS_append = "${@['', ' ${SOC_VARIANT_ARCH}'][d.getVar('SOC_VARIANT_ARCH') != d.getVar('SOC_FAMILY_ARCH')]}"
