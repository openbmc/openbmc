SRCBRANCH ?= "release/2.40/master"
PV = "2.40+git"
SRCREV_glibc ?= "efb710034e4c5e734d100cc4ef1b1e27d4315825"
SRCREV_localedef ?= "fab74f31b3811df543e24b6de47efdf45b538abc"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
