#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

# Class for generating signed RPM packages.
#
# Configuration variables used by this class:
# RPM_GPG_PASSPHRASE
#           The passphrase of the signing key.
# RPM_GPG_NAME
#           Name of the key to sign with. May be key id or key name.
# RPM_GPG_BACKEND
#           Optional variable for specifying the backend to use for signing.
#           Currently the only available option is 'local', i.e. local signing
#           on the build host.
# RPM_FILE_CHECKSUM_DIGEST
#           Optional variable for specifying the algorithm for generating file
#           checksum digest.
# RPM_FSK_PATH
#           Optional variable for the file signing key.
# RPM_FSK_PASSWORD
#           Optional variable for the file signing key password.
# GPG_BIN
#           Optional variable for specifying the gpg binary/wrapper to use for
#           signing.
# RPM_GPG_SIGN_CHUNK
#           Optional variable indicating the number of packages used per gpg
#           invocation
# GPG_PATH
#           Optional variable for specifying the gnupg "home" directory:

inherit sanity

RPM_SIGN_PACKAGES='1'
RPM_SIGN_FILES ?= '0'
RPM_GPG_BACKEND ?= 'local'
# SHA-256 is used by default
RPM_FILE_CHECKSUM_DIGEST ?= '8'
RPM_GPG_SIGN_CHUNK ?= "${BB_NUMBER_THREADS}"


python () {
    if d.getVar('RPM_GPG_PASSPHRASE_FILE'):
        raise_sanity_error('RPM_GPG_PASSPHRASE_FILE is replaced by RPM_GPG_PASSPHRASE', d)
    # Check configuration
    for var in ('RPM_GPG_NAME', 'RPM_GPG_PASSPHRASE'):
        if not d.getVar(var):
            raise_sanity_error("You need to define %s in the config" % var, d)

    if d.getVar('RPM_SIGN_FILES') == '1':
        for var in ('RPM_FSK_PATH', 'RPM_FSK_PASSWORD'):
            if not d.getVar(var):
                raise_sanity_error("You need to define %s in the config" % var, d)
}

python sign_rpm () {
    import glob
    from oe.gpg_sign import get_signer

    signer = get_signer(d, d.getVar('RPM_GPG_BACKEND'))
    rpms = glob.glob(d.getVar('RPM_PKGWRITEDIR') + '/*')

    signer.sign_rpms(rpms,
                     d.getVar('RPM_GPG_NAME'),
                     d.getVar('RPM_GPG_PASSPHRASE'),
                     d.getVar('RPM_FILE_CHECKSUM_DIGEST'),
                     int(d.getVar('RPM_GPG_SIGN_CHUNK')),
                     d.getVar('RPM_FSK_PATH'),
                     d.getVar('RPM_FSK_PASSWORD'))
}
sign_rpm[vardepsexclude] += "RPM_GPG_SIGN_CHUNK"

do_package_index[depends] += "signing-keys:do_deploy"
do_rootfs[depends] += "signing-keys:do_populate_sysroot"

PACKAGE_WRITE_DEPS += "gnupg-native"
