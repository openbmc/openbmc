SUMMARY = "xbindkeys - Tool for launching commands on keystrokes"
HOMEPAGE = "http://www.nongnu.org/xbindkeys/xbindkeys.html"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=7d715d0b23c4bd9265b7435f546e9fe8"
SECTION = "x11"

DEPENDS = "virtual/libx11"

inherit features_check autotools

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://www.nongnu.org/xbindkeys/xbindkeys-${PV}.tar.gz"
SRC_URI[sha256sum] = "a29b86a8ec91d4abc83b420e547da27470847d0efe808aa6e75147aa0adb82f2"

EXTRA_OECONF = "--disable-guile"
