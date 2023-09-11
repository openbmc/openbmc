SRCBRANCH ?= "release/2.38/master"
PV = "2.38+git"
SRCREV_glibc ?= "1aed90c9c8f8be9f68b58e96b6e4cd0fc08eb2b1"
SRCREV_localedef ?= "e0eca29583b9e0f62645c4316ced93cf4e4e26e1"

GLIBC_GIT_URI ?= "git://sourceware.org/git/glibc.git;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.(?!90)\d+)*)"
