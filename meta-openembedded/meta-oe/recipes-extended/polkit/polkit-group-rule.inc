# polkit must prepare polkitd group
DEPENDS += "polkit"

inherit features_check
REQUIRED_DISTRO_FEATURES = "polkit"

inherit useradd

FILES:${PN} += "${datadir}/polkit-1/rules.d"
