SRCBRANCH ?= "release/2.35/master"
PV = "2.35"
SRCREV_glibc ?= "b6aade18a7e5719c942aa2da6cf3157aca993fa4"
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
