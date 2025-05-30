require recipes-security/optee/optee-client.inc

# v4.4.0
SRCREV = "d221676a58b305bddbf97db00395205b3038de8e"
SRC_URI += "file://0001-tee-supplicant-update-udev-systemd-install-code.patch"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
