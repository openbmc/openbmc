SUMMARY = "Phosphor LED Group Management meta data"
PR = "r1"

inherit obmc-phosphor-utils
inherit obmc-phosphor-license

# Default is the example set of data.
PHOSPHOR_LED_MANAGER_CONFIG ??= "${PN}-example-native"

# Choose appropriate yaml file
def get_depends(d):
    if d.getVar('USE_MRW', 'yes'):
        return "${PN}-mrw-native"
    else:
        return "${PHOSPHOR_LED_MANAGER_CONFIG}"

USE_MRW = "${@cf_enabled(d, 'obmc-mrw', 'yes')}"
DEPENDS += "${@get_depends(d)}"
