SRCBRANCH ?= "release/2.32/master"
PV = "2.32"
SRCREV_glibc ?= "760e1d287825fa91d4d5a0cc921340c740d803e2"
SRCREV_localedef ?= "bd644c9e6f3e20c5504da1488448173c69c56c28"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
