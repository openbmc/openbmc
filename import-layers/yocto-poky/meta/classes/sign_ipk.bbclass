# Class for generating signed IPK packages.
#
# Configuration variables used by this class:
# IPK_GPG_PASSPHRASE_FILE
#           Path to a file containing the passphrase of the signing key.
# IPK_GPG_NAME
#           Name of the key to sign with.
# IPK_GPG_BACKEND
#           Optional variable for specifying the backend to use for signing.
#           Currently the only available option is 'local', i.e. local signing
#           on the build host.
# IPK_GPG_SIGNATURE_TYPE
#           Optional variable for specifying the type of gpg signatures, can be:
#                     1. Ascii armored (ASC), default if not set
#                     2. Binary (BIN)
# GPG_BIN
#           Optional variable for specifying the gpg binary/wrapper to use for
#           signing.
# GPG_PATH
#           Optional variable for specifying the gnupg "home" directory:
#

inherit sanity

IPK_SIGN_PACKAGES = '1'
IPK_GPG_BACKEND ?= 'local'
IPK_GPG_SIGNATURE_TYPE ?= 'ASC'

python () {
    # Check configuration
    for var in ('IPK_GPG_NAME', 'IPK_GPG_PASSPHRASE_FILE'):
        if not d.getVar(var):
            raise_sanity_error("You need to define %s in the config" % var, d)

    sigtype = d.getVar("IPK_GPG_SIGNATURE_TYPE")
    if sigtype.upper() != "ASC" and sigtype.upper() != "BIN":
        raise_sanity_error("Bad value for IPK_GPG_SIGNATURE_TYPE (%s), use either ASC or BIN" % sigtype)
}

def sign_ipk(d, ipk_to_sign):
    from oe.gpg_sign import get_signer

    bb.debug(1, 'Signing ipk: %s' % ipk_to_sign)

    signer = get_signer(d, d.getVar('IPK_GPG_BACKEND'))
    sig_type = d.getVar('IPK_GPG_SIGNATURE_TYPE')
    is_ascii_sig = (sig_type.upper() != "BIN")

    signer.detach_sign(ipk_to_sign,
                       d.getVar('IPK_GPG_NAME'),
                       d.getVar('IPK_GPG_PASSPHRASE_FILE'),
                       armor=is_ascii_sig)
