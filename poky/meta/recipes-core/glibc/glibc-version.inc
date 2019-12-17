SRCBRANCH ?= "release/2.30/master"
PV = "2.30"
SRCREV_glibc ?= "be9a328c93834648e0bec106a1f86357d1a8c7e1"
SRCREV_localedef ?= "cd9f958c4c94a638fa7b2b4e21627364f1a1a655"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
