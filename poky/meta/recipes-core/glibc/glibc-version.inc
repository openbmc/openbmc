SRCBRANCH ?= "release/2.31/master"
PV = "2.31+git${SRCPV}"
SRCREV_glibc ?= "6fdf971c9dbf7dac9bea552113fe4694015bbc4d"
SRCREV_localedef ?= "cd9f958c4c94a638fa7b2b4e21627364f1a1a655"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
