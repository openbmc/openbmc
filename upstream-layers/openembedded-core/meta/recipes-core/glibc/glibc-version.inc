SRCBRANCH ?= "release/2.43/master"
PV = "2.43+git"
SRCREV_glibc ?= "e9517114acc77c0b8af9da6700107df16882b701"
SRCREV_localedef ?= "cba02c503d7c853a38ccfb83c57e343ca5ecd7e5"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
