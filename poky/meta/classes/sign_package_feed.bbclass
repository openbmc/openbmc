# Class for signing package feeds
#
# Related configuration variables that will be used after this class is
# iherited:
# PACKAGE_FEED_PASSPHRASE_FILE
#           Path to a file containing the passphrase of the signing key.
# PACKAGE_FEED_GPG_NAME
#           Name of the key to sign with. May be key id or key name.
# PACKAGE_FEED_GPG_BACKEND
#           Optional variable for specifying the backend to use for signing.
#           Currently the only available option is 'local', i.e. local signing
#           on the build host.
# PACKAGE_FEED_GPG_SIGNATURE_TYPE
#           Optional variable for specifying the type of gpg signature, can be:
#               1. Ascii armored (ASC), default if not set
#               2. Binary (BIN)
#           This variable is only available for IPK feeds. It is ignored on
#           other packaging backends.
# GPG_BIN
#           Optional variable for specifying the gpg binary/wrapper to use for
#           signing.
# GPG_PATH
#           Optional variable for specifying the gnupg "home" directory:
#
inherit sanity

PACKAGE_FEED_SIGN = '1'
PACKAGE_FEED_GPG_BACKEND ?= 'local'
PACKAGE_FEED_GPG_SIGNATURE_TYPE ?= 'ASC'
PACKAGEINDEXDEPS += "gnupg-native:do_populate_sysroot"

# Make feed signing key to be present in rootfs
FEATURE_PACKAGES_package-management:append = " signing-keys-packagefeed"

python () {
    # Check sanity of configuration
    for var in ('PACKAGE_FEED_GPG_NAME', 'PACKAGE_FEED_GPG_PASSPHRASE_FILE'):
        if not d.getVar(var):
            raise_sanity_error("You need to define %s in the config" % var, d)

    sigtype = d.getVar("PACKAGE_FEED_GPG_SIGNATURE_TYPE")
    if sigtype.upper() != "ASC" and sigtype.upper() != "BIN":
        raise_sanity_error("Bad value for PACKAGE_FEED_GPG_SIGNATURE_TYPE (%s), use either ASC or BIN" % sigtype)
}

do_package_index[depends] += "signing-keys:do_deploy"
do_rootfs[depends] += "signing-keys:do_populate_sysroot gnupg-native:do_populate_sysroot"
