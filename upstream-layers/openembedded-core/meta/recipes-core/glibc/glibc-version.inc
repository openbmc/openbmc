SRCBRANCH ?= "release/2.44/master"
PV = "2.44+git"
SRCREV_glibc ?= "5c479454d1232f71c78fa21584e90a2f57883407"
SRCREV_localedef ?= "cba02c503d7c853a38ccfb83c57e343ca5ecd7e5"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
