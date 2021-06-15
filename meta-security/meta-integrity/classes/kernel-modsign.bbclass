# No default! Either this or MODSIGN_PRIVKEY/MODSIGN_X509 have to be
# set explicitly in a local.conf before activating kernel-modsign.
# To use the insecure (because public) example keys, use
# MODSIGN_KEY_DIR = "${INTEGRITY_BASE}/data/debug-keys"
MODSIGN_KEY_DIR ?= "MODSIGN_KEY_DIR_NOT_SET"

# Private key for modules signing. The default is okay when
# using the example key directory.
MODSIGN_PRIVKEY ?= "${MODSIGN_KEY_DIR}/privkey_modsign.pem"

# Public part of certificates used for modules signing.
# The default is okay when using the example key directory.
MODSIGN_X509 ?= "${MODSIGN_KEY_DIR}/x509_modsign.crt"

# If this class is enabled, disable stripping signatures from modules
INHIBIT_PACKAGE_STRIP = "1"

kernel_do_configure_prepend() {
    if [ -f "${MODSIGN_PRIVKEY}" -a -f "${MODSIGN_X509}" ]; then
        cat "${MODSIGN_PRIVKEY}" "${MODSIGN_X509}" \
            > "${B}/modsign_key.pem"
    else
        bberror "Either modsign key or certificate are invalid"
    fi
}

do_shared_workdir_append() {
    cp modsign_key.pem $kerneldir/
}
