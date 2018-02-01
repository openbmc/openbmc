require ${BPN}.inc

SRC_URI = "\
    ${E_MIRROR}/${SRCNAME}-${SRCVER}.tar.gz \
"

SRC_URI[md5sum] = "954fe8e40fec6a561190ff0fb75b6bdd"
SRC_URI[sha256sum] = "a05be096c911e0d66d4bdc497ebb935a04ad23696de9084aed9959b5172a593e"

PNBLACKLIST[expedite] ?= "Depends on blacklisted eet - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Depends on blacklisted evas - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-loader-png - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted expedite - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted expedite-themes - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-gl-x11 - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-software-generic - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted expedite-dev - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-software-x11 - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-fb - the recipe will be removed on 2017-09-01 unless the issue is fixed"
