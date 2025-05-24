SRCBRANCH ?= "release/2.41/master"
PV = "2.41+git"
SRCREV_glibc ?= "5b4c4617016d28569106549dff6f9fec73eed5ce"
SRCREV_localedef ?= "fab74f31b3811df543e24b6de47efdf45b538abc"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
