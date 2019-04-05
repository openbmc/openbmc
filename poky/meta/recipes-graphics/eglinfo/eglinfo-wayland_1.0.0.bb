EGLINFO_PLATFORM ?= "wayland"
EGLINFO_BINARY_NAME ?= "eglinfo-wayland"

require eglinfo.inc

DEPENDS += "wayland"

inherit distro_features_check

# depends on wayland
REQUIRED_DISTRO_FEATURES += "wayland"

SUMMARY += "(Wayland version)"
