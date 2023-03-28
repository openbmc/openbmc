SRCBRANCH ?= "release/2.37/master"
PV = "2.37"
SRCREV_glibc ?= "d8e1a7590d375159fb5aac07ad8111ab4699e994" 
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
