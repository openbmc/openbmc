require recipes-security/optee/optee-client.inc

SRCREV = "dd2d39b49975d2ada7870fe2b7f5a84d0d3860dc"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
