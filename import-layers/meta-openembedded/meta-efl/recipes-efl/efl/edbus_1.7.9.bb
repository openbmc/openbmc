require ${BPN}.inc

SRC_URI = "\
    ${E_MIRROR}/${SRCNAME}-${SRCVER}.tar.gz \
"

SRC_URI[md5sum] = "8f72da14e5664aad8c45eeeca0e3ff5f"
SRC_URI[sha256sum] = "c328c4cf1424629cb67c83689ccc1f95967abcb4c03dffd437e93b799ff151c1"

PNBLACKLIST[edbus] ?= "Depends on blacklisted ecore - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[edbus] ?= "Depends on blacklisted eina - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[edbus] ?= "Runtime depends on blacklisted edbus - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[edbus] ?= "Runtime depends on blacklisted edbus-dev - the recipe will be removed on 2017-09-01 unless the issue is fixed"
