
FILESEXTRAPATHS:prepend:olympus-nuvoton := "${THISDIR}/${PN}:"

SRC_URI:append:olympus-nuvoton = " \
           file://pam_succeed_if_support_ldap_user_login.patch \
           "
