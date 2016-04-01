# Class for signing package feeds
#
# Related configuration variables that will be used after this class is
# iherited:
# PACKAGE_FEED_PASSPHRASE_FILE
#           Path to a file containing the passphrase of the signing key.
# PACKAGE_FEED_GPG_NAME
#           Name of the key to sign with. May be key id or key name.
# GPG_BIN
#           Optional variable for specifying the gpg binary/wrapper to use for
#           signing.
# GPG_PATH
#           Optional variable for specifying the gnupg "home" directory:
#
inherit sanity

PACKAGE_FEED_SIGN = '1'

python () {
    # Check sanity of configuration
    for var in ('PACKAGE_FEED_GPG_NAME', 'PACKAGE_FEED_GPG_PASSPHRASE_FILE'):
        if not d.getVar(var, True):
            raise_sanity_error("You need to define %s in the config" % var, d)

    # Set expected location of the public key
    d.setVar('PACKAGE_FEED_GPG_PUBKEY',
             os.path.join(d.getVar('STAGING_ETCDIR_NATIVE'),
                                   'PACKAGE-FEED-GPG-PUBKEY'))
}

do_package_index[depends] += "signing-keys:do_export_public_keys"
