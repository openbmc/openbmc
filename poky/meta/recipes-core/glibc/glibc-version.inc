SRCBRANCH ?= "release/2.40/master"
PV = "2.40+git"
SRCREV_glibc ?= "626c048f32a979f77662bdcb1cca477c11d3f9c1"
SRCREV_localedef ?= "fab74f31b3811df543e24b6de47efdf45b538abc"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
