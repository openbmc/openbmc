require ${BPN}.inc

SRC_URI = "\
    ${E_RELEASES}/apps/${SRCNAME}/${SRCNAME}-${SRCVER}.tar.gz \
"

SRC_URI[md5sum] = "c7ce2e8ebc5f311d3d3f59ecfdf18f61"
SRC_URI[sha256sum] = "7fb864a14202490e9181c5f254a7e772019216a3aa75c3952d0f12cd32113896"

PNBLACKLIST[terminology] ?= "Depends on blacklisted elementary - the recipe will be removed on 2017-09-01 unless the issue is fixed"
