SRCBRANCH ?= "release/2.43/master"
PV = "2.43+git"
SRCREV_glibc ?= "562cd7badd3f9edd74b68316439f910f6f7c4c1f"
SRCREV_localedef ?= "cba02c503d7c853a38ccfb83c57e343ca5ecd7e5"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
