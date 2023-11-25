require recipes-security/optee/optee-client.inc

SRCREV = "8533e0e6329840ee96cf81b6453f257204227e6c"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
