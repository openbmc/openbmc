EGLINFO_PLATFORM ?= "x11"
EGLINFO_BINARY_NAME ?= "eglinfo-x11"

include eglinfo.inc

DEPENDS += "virtual/libx11"

inherit distro_features_check
# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SUMMARY += "(X11 version)"
