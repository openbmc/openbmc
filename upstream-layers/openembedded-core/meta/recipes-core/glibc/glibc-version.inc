SRCBRANCH ?= "release/2.43/master"
PV = "2.43+git"
SRCREV_glibc ?= "ce1013a197eb4a3b8ff2b07e0672f4d0b976ce7c"
SRCREV_localedef ?= "cba02c503d7c853a38ccfb83c57e343ca5ecd7e5"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
