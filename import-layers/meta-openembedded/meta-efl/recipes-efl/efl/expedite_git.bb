require ${BPN}.inc

SRCREV = "a5e6af917af52877b378090811cf836c16d0bfbb"
PV = "1.7.99+gitr${SRCPV}"

DEFAULT_PREFERENCE = "-1"

SRC_URI = "\
    git://git.enlightenment.org/tools/${BPN}.git \
"
S = "${WORKDIR}/${SRCNAME}"

PNBLACKLIST[expedite] ?= "Depends on blacklisted eet - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Depends on blacklisted evas - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-loader-png - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted expedite - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-software-generic - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-fb - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-software-x11 - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted evas-engine-gl-x11 - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted expedite-dev - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[expedite] ?= "Runtime depends on blacklisted expedite-themes - the recipe will be removed on 2017-09-01 unless the issue is fixed"
