SRCBRANCH ?= "release/2.36/master"
PV = "2.36"
SRCREV_glibc ?= "3bd3c612e98a53ce60ed972f5cd2b90628b3cba5"
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
