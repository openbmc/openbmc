# polkit must prepare polkitd group
DEPENDS += "polkit"

inherit features_check
REQUIRED_DISTRO_FEATURES = "polkit"

inherit useradd

do_install:prepend() {
        install -m 755 -d ${D}${datadir}/polkit-1/rules.d
}

FILES:${PN} += "${datadir}/polkit-1/rules.d"
