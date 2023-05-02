SRCBRANCH ?= "release/2.35/master"
PV = "2.35"
SRCREV_glibc ?= "293211b6fddf60fc407d21fcba0326dd2148f76b"
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
