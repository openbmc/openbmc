SRCBRANCH ?= "release/2.42/master"
PV = "2.42+git"
SRCREV_glibc ?= "912d89a766847649a3857985a3b5e6065c51bfd4"
SRCREV_localedef ?= "cba02c503d7c853a38ccfb83c57e343ca5ecd7e5"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
