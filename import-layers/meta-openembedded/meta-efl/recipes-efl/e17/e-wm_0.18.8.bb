require ${BPN}.inc

SRCNAME = "enlightenment"

S = "${WORKDIR}/${SRCNAME}-${PV}"

SRC_URI = "\
    ${E_RELEASES}/apps/${SRCNAME}/${SRCNAME}-${SRCVER}.tar.gz \
    file://enlightenment_start.oe \
    file://applications.menu \
    file://0001-Fix-incorrect-message-type.patch \
"

SRC_URI[md5sum] = "79c9f524e1d0510061c62c4b038a8ece"
SRC_URI[sha256sum] = "14c9bde4334d2f8b0776c6113d02b923ab159eea1cbf7013489e4f3bf37a51bb"

PNBLACKLIST[e-wm] ?= "Depends on blacklisted elementary - the recipe will be removed on 2017-09-01 unless the issue is fixed"
