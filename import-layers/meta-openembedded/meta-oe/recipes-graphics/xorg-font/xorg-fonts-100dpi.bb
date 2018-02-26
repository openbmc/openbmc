SUMMARY = "Xorg 100 DPI font set"
LICENSE = "Custom"

inherit packagegroup distro_features_check
# rdepends on font recipes with this restriction
REQUIRED_DISTRO_FEATURES = "x11"

RDEPENDS_${PN} = "\
    font-adobe-100dpi \
    font-adobe-utopia-100dpi \
    font-bh-100dpi \
    font-bh-lucidatypewriter-100dpi \
    font-bitstream-100dpi \
"
