SRCBRANCH ?= "release/2.36/master"
PV = "2.36"
SRCREV_glibc ?= "c399271c10bd00714504e8d4dfbec8aebf996dd4"
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
