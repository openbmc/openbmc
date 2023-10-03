SRCBRANCH ?= "release/2.37/master"
PV = "2.37"
SRCREV_glibc ?= "58f7431fd77c0a6dd8df08d50c51ee3e7f09825f" 
SRCREV_localedef ?= "794da69788cbf9bf57b59a852f9f11307663fa87"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
