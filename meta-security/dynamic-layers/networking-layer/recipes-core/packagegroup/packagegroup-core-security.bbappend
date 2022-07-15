
RDEPENDS:packagegroup-security-utils += "\
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sssd", "",d)} \
"
