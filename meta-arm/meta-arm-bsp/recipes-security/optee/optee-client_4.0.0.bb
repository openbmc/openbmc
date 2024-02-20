require recipes-security/optee/optee-client.inc

SRCREV = "acb0885c117e73cb6c5c9b1dd9054cb3f93507ee"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
