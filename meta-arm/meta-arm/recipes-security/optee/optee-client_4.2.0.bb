require recipes-security/optee/optee-client.inc

SRCREV = "3eac340a781c00ccd61b151b0e9c22a8c6e9f9f0"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
