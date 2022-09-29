# Remove cracklib from PAM, to avoid breaking PAM and further dependencies
# This allows cracklib to be neatly severed from the system.

EXTRA_OECONF:append:gbmc = " --disable-cracklib"

DEPENDS:remove:gbmc = "cracklib"

RDEPENDS:${PN}-runtime:remove:gbmc = "pam-plugin-cracklib-suffix"

RDEPENDS:${PN}-xtests:remove:gbmc = "${MLPREFIX}pam-plugin-cracklib-${libpam_suffix}"

do_install:append:gbmc() {
    # Remove reference to cracklib library from PAM config file
    sed -i '/pam_cracklib.so/d' ${D}${sysconfdir}/pam.d/common-password
}
