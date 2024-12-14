require recipes-security/optee/optee-client.inc

SRCREV = "a5b1ffcd26e328af0bbf18ab448a38ecd558e05c"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
