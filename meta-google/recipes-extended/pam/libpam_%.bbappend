# Remove cracklib from PAM, to avoid breaking PAM and further dependencies
# This allows cracklib to be neatly severed from the system.

EXTRA_OECONF:append:gbmc = " --disable-cracklib"

DEPENDS:remove:gbmc = "cracklib"

RDEPENDS:${PN}-runtime:remove:gbmc = "pam-plugin-cracklib-suffix"

RDEPENDS:${PN}-xtests:remove:gbmc = "${MLPREFIX}pam-plugin-cracklib-${libpam_suffix}"

do_install:append:gbmc() {
    # Remove reference to cracklib library from PAM config file
    sed -i '/pam_cracklib.so/d' ${D}${sysconfdir}/pam.d/common-password

    # Remove the first occurrence of "use_authtok" in the first line starting
    # with "password". This makes sure that if pam_cracklib.so was the first
    # entry, we didn't invalidate the next entry in the stack. If the first
    # entry has the "use_authtok" set, this "forces the module to not prompt
    # the user for a new password but use the one provided by the previously
    # stacked password module". Since there is no "previous" entry, it never
    # asks for a password which causes the process to fail.
    awk '/^password/ && !f{sub(/ use_authtok/, ""); f=1} 1' \
        ${D}${sysconfdir}/pam.d/common-password \
        > ${D}${sysconfdir}/pam.d/common-password.new
    mv ${D}${sysconfdir}/pam.d/common-password.new \
        ${D}${sysconfdir}/pam.d/common-password
}
