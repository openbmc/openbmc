require recipes-security/optee/optee-client.inc

SRCREV = "a5b1ffcd26e328af0bbf18ab448a38ecd558e05c"

SRC_URI += "file://0001-tee-supplicant-add-udev-rule-and-systemd-service-fil.patch \
            file://0001-tee-supplicant-update-udev-systemd-install-code.patch"

inherit pkgconfig
DEPENDS += "util-linux"
EXTRA_OEMAKE += "PKG_CONFIG=pkg-config"
