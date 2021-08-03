SRCBRANCH ?= "release/2.31/master"
PV = "2.31+git${SRCPV}"
SRCREV_glibc ?= "4f0a61f75385c9a5879cbe7202042e88f692a3c8"
SRCREV_localedef ?= "cd9f958c4c94a638fa7b2b4e21627364f1a1a655"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
