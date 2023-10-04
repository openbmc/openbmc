SUMMARY = "Basic X11 session"
DESCRIPTION = "Packages required to set up a basic working X11 session"

inherit packagegroup features_check
# rdepends on matchbox-wm
REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS:${PN} = "\
    packagegroup-core-x11-xserver \
    packagegroup-core-x11-utils \
    matchbox-terminal \
    matchbox-wm \
    mini-x-session \
    liberation-fonts \
    "
