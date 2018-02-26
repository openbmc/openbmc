SUMMARY = "Host SDK package for Go cross canadian toolchain"
PN = "packagegroup-go-cross-canadian-${MACHINE}"

inherit cross-canadian packagegroup

PACKAGEGROUP_DISABLE_COMPLEMENTARY = "1"

GO = "go-cross-canadian-${TRANSLATED_TARGET_ARCH}"

RDEPENDS_${PN} = " \
    ${@all_multilib_tune_values(d, 'GO')} \
"
