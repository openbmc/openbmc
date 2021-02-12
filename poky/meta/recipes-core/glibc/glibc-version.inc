SRCBRANCH ?= "release/2.33/master"
PV = "2.33"
SRCREV_glibc ?= "9826b03b747b841f5fc6de2054bf1ef3f5c4bdf3"
SRCREV_localedef ?= "bd644c9e6f3e20c5504da1488448173c69c56c28"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
