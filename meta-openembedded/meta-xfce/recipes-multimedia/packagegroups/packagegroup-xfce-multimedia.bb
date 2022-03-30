SUMMARY = "All packages multimedia packages available for XFCE"

inherit packagegroup features_check

# parole needs x11
REQUIRED_DISTRO_FEATURES = "pam x11"

# While this item does not require it, it depends on xfmpc and xfc4-mpc-plugin
# that wants mpd which does
LICENSE_FLAGS = "commercial"

RDEPENDS:${PN} = " \
    parole \
    xfmpc \
    xfce4-mpc-plugin \
"
