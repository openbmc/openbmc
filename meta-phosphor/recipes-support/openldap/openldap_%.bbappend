FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://0004-ITS-10498-libldap-fix-for-OpenSSL-4-compatibility.patch \
    "

PACKAGECONFIG = "openssl modules \
                ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
