inherit xfce features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://archive.xfce.org/src/apps/${BPN}/${@xfce_verdir("${PV}")}/${BPN}-${PV}.tar.${XFCE_COMPRESS_TYPE}"

