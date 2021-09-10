SRCBRANCH ?= "release/2.34/master"
PV = "2.34"
SRCREV_glibc ?= "ae37d06c7d127817ba43850f0f898b793d42aea7"
SRCREV_localedef ?= "95c0221703ad970a52445e9eaf91c4aff35eebef"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
