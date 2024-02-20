require recipes-security/optee/optee-client.inc

SRCREV = "f7e4ced15d1fefd073bbfc484fe0e1f74afe96c2"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
