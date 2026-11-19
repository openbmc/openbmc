FILESEXTRAPATHS:prepend:df-google-authenticator-libpam := "${THISDIR}/${BPN}:"
SRC_URI:append:df-google-authenticator-libpam = " \
    file://pam.d/common-account \
    file://pam.d/common-auth \
"
