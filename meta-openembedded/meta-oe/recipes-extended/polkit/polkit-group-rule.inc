# polkit must prepare polkitd group
DEPENDS += "polkit"

inherit features_check
REQUIRED_DISTRO_FEATURES = "polkit"

inherit useradd

do_install:prepend() {
    install -m 700 -d ${D}${sysconfdir}/polkit-1/rules.d
    chown polkitd:root ${D}/${sysconfdir}/polkit-1/rules.d
}
USERADD_PARAM:${PN}:prepend = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 polkitd;"
