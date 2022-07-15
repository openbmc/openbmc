# Copyright (C) 2015 Intel Corporation
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Makes public keys of the signing keys available"
LICENSE = "MIT"


inherit allarch deploy

EXCLUDE_FROM_WORLD = "1"
INHIBIT_DEFAULT_DEPS = "1"

SYSROOT_DIRS += "${sysconfdir}/pki"

PACKAGES =+ "${PN}-ipk ${PN}-rpm ${PN}-packagefeed"

FILES:${PN}-rpm = "${sysconfdir}/pki/rpm-gpg"
FILES:${PN}-ipk = "${sysconfdir}/pki/ipk-gpg"
FILES:${PN}-packagefeed = "${sysconfdir}/pki/packagefeed-gpg"

RDEPENDS:${PN}-dev = ""

python do_get_public_keys () {
    from oe.gpg_sign import get_signer

    if d.getVar("RPM_SIGN_PACKAGES"):
        # Export public key of the rpm signing key
        signer = get_signer(d, d.getVar('RPM_GPG_BACKEND'))
        signer.export_pubkey(os.path.join(d.expand('${B}'), 'rpm-key'),
                             d.getVar('RPM_GPG_NAME'))

    if d.getVar("IPK_SIGN_PACKAGES"):
        # Export public key of the ipk signing key
        signer = get_signer(d, d.getVar('IPK_GPG_BACKEND'))
        signer.export_pubkey(os.path.join(d.expand('${B}'), 'ipk-key'),
                             d.getVar('IPK_GPG_NAME'))

    if d.getVar('PACKAGE_FEED_SIGN') == '1':
        # Export public key of the feed signing key
        signer = get_signer(d, d.getVar('PACKAGE_FEED_GPG_BACKEND'))
        signer.export_pubkey(os.path.join(d.expand('${B}'), 'pf-key'),
                             d.getVar('PACKAGE_FEED_GPG_NAME'))
}
do_get_public_keys[cleandirs] = "${B}"
addtask get_public_keys before do_install
do_get_public_keys[depends] += "gnupg-native:do_populate_sysroot"

do_install () {
    if [ -f "${B}/rpm-key" ]; then
        install -D -m 0644 "${B}/rpm-key" "${D}${sysconfdir}/pki/rpm-gpg/RPM-GPG-KEY-${DISTRO}-${DISTRO_CODENAME}"
    fi
    if [ -f "${B}/ipk-key" ]; then
        install -D -m 0644 "${B}/ipk-key" "${D}${sysconfdir}/pki/ipk-gpg/IPK-GPG-KEY-${DISTRO}-${DISTRO_CODENAME}"
    fi
    if [ -f "${B}/pf-key" ]; then
        install -D -m 0644 "${B}/pf-key" "${D}${sysconfdir}/pki/packagefeed-gpg/PACKAGEFEED-GPG-KEY-${DISTRO}-${DISTRO_CODENAME}"
    fi
}

do_deploy () {
    if [ -f "${B}/rpm-key" ]; then
        install -D -m 0644 "${B}/rpm-key" "${DEPLOYDIR}/RPM-GPG-KEY-${DISTRO}-${DISTRO_CODENAME}"
    fi
    if [ -f "${B}/ipk-key" ]; then
        install -D -m 0644 "${B}/ipk-key" "${DEPLOYDIR}/IPK-GPG-KEY-${DISTRO}-${DISTRO_CODENAME}"
    fi
    if [ -f "${B}/pf-key" ]; then
        install -D -m 0644 "${B}/pf-key" "${DEPLOYDIR}/PACKAGEFEED-GPG-KEY-${DISTRO}-${DISTRO_CODENAME}"
    fi
}
do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_RPM}"
# clear stamp-extra-info since MACHINE_ARCH is normally put there by
# deploy.bbclass
do_deploy[stamp-extra-info] = ""
addtask deploy after do_get_public_keys

# Delete unnecessary tasks. In particular, "do_unpack" _must_ be deleted because
# it cleans ${B} and will wipe any keys exported by do_get_public_keys.
deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile
