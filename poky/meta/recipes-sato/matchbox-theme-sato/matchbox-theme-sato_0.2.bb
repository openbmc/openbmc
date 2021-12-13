require matchbox-theme-sato.inc

# SRCREV tagged 0.2
SRCREV = "df085ba9cdaeaf2956890b0e29d7ea1779bf6c78"
SRC_URI = "git://git.yoctoproject.org/matchbox-sato;branch=master"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"
