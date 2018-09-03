PACKAGECONFIG = "openssl modules \
                ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
