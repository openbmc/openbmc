require optee-os-3_19.inc

DEPENDS += "dtc-native"

SRCREV = "afacf356f9593a7f83cae9f96026824ec242ff52"

SRC_URI:append = " \
    file://0001-core-Define-section-attributes-for-clang.patch \ 
    "
