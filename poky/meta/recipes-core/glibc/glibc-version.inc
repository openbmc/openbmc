SRCBRANCH ?= "release/2.41/master"
PV = "2.41+git"
SRCREV_glibc ?= "0a7c7a3e283a55d1bfaa48fdef063a32a4689a2b"
SRCREV_localedef ?= "fab74f31b3811df543e24b6de47efdf45b538abc"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
