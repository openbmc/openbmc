# Copyright (C) 2015 Intel Corporation
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Make public keys of the signing keys available"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690 \
                    file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"


inherit allarch deploy

EXCLUDE_FROM_WORLD = "1"
INHIBIT_DEFAULT_DEPS = "1"

PACKAGES =+ "${PN}-ipk ${PN}-rpm ${PN}-packagefeed"

FILES_${PN}-rpm = "${sysconfdir}/pki/rpm-gpg"
FILES_${PN}-ipk = "${sysconfdir}/pki/ipk-gpg"
FILES_${PN}-packagefeed = "${sysconfdir}/pki/packagefeed-gpg"

python do_get_public_keys () {
    from oe.gpg_sign import get_signer

    if d.getVar("RPM_SIGN_PACKAGES", True):
        # Export public key of the rpm signing key
        signer = get_signer(d, d.getVar('RPM_GPG_BACKEND', True))
        signer.export_pubkey(os.path.join(d.expand('${B}'), 'rpm-key'),
                             d.getVar('RPM_GPG_NAME', True))

    if d.getVar("IPK_SIGN_PACKAGES", True):
        # Export public key of the ipk signing key
        signer = get_signer(d, d.getVar('IPK_GPG_BACKEND', True))
        signer.export_pubkey(os.path.join(d.expand('${B}'), 'ipk-key'),
                             d.getVar('IPK_GPG_NAME', True))

    if d.getVar('PACKAGE_FEED_SIGN', True) == '1':
        # Export public key of the feed signing key
        signer = get_signer(d, d.getVar('PACKAGE_FEED_GPG_BACKEND', True))
        signer.export_pubkey(os.path.join(d.expand('${B}'), 'pf-key'),
                             d.getVar('PACKAGE_FEED_GPG_NAME', True))
}
do_get_public_keys[cleandirs] = "${B}"
addtask get_public_keys before do_install

do_install () {
    if [ -f "${B}/rpm-key" ]; then
        install -D -m 0644 "${B}/rpm-key" "${D}${sysconfdir}/pki/rpm-gpg/RPM-GPG-KEY-${DISTRO_VERSION}"
    fi
    if [ -f "${B}/ipk-key" ]; then
        install -D -m 0644 "${B}/ipk-key" "${D}${sysconfdir}/pki/ipk-gpg/IPK-GPG-KEY-${DISTRO_VERSION}"
    fi
    if [ -f "${B}/pf-key" ]; then
        install -D -m 0644 "${B}/pf-key" "${D}${sysconfdir}/pki/packagefeed-gpg/PACKAGEFEED-GPG-KEY-${DISTRO_VERSION}"
    fi
}

sysroot_stage_all_append () {
    sysroot_stage_dir ${D}${sysconfdir}/pki ${SYSROOT_DESTDIR}${sysconfdir}/pki
}

do_deploy () {
    if [ -f "${B}/rpm-key" ]; then
        install -D -m 0644 "${B}/rpm-key" "${DEPLOYDIR}/RPM-GPG-KEY-${DISTRO_VERSION}"
    fi
    if [ -f "${B}/ipk-key" ]; then
        install -D -m 0644 "${B}/ipk-key" "${DEPLOYDIR}/IPK-GPG-KEY-${DISTRO_VERSION}"
    fi
    if [ -f "${B}/pf-key" ]; then
        install -D -m 0644 "${B}/pf-key" "${DEPLOYDIR}/PACKAGEFEED-GPG-KEY-${DISTRO_VERSION}"
    fi
}
do_deploy[sstate-outputdirs] = "${DEPLOY_DIR_RPM}"
# cleandirs should possibly be in deploy.bbclass but we need it
do_deploy[cleandirs] = "${DEPLOYDIR}"
# clear stamp-extra-info since MACHINE is normally put there by deploy.bbclass
do_deploy[stamp-extra-info] = ""
addtask deploy after do_get_public_keys
