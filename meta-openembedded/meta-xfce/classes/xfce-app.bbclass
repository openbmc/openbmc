inherit xfce distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://archive.xfce.org/src/apps/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.bz2"

