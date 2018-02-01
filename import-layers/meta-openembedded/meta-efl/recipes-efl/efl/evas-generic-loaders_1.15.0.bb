require ${BPN}.inc

SRC_URI = "\
    ${E_RELEASES}/libs/${SRCNAME}/${SRCNAME}-${SRCVER}.tar.gz \
"

SRC_URI[md5sum] = "3ca8443b8cbf177845595c5e02fbc49c"
SRC_URI[sha256sum] = "caa22c9ba1ae9629c16a3fe809ea927f60b8f0d80cdb7f145159b997b9ae2bcd"

PNBLACKLIST[evas-generic-loaders] ?= "Depends on blacklisted eina - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[evas-generic-loaders] ?= "Runtime depends on blacklisted evas-generic-loaders - the recipe will be removed on 2017-09-01 unless the issue is fixed"

PNBLACKLIST[evas-generic-loaders] ?= "Runtime depends on blacklisted evas-generic-loaders-dev - the recipe will be removed on 2017-09-01 unless the issue is fixed"
