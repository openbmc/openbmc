require recipes-security/optee/optee-client.inc

# v4.6.0
SRCREV = "02e7f9213b0d7db9c35ebf1e41e733fc9c5a3f75"
SRC_URI += "file://0001-tee-supplicant-update-udev-systemd-install-code.patch"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
