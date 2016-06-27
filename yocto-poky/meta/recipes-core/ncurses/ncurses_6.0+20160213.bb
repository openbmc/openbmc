require ncurses.inc

SRC_URI += "file://tic-hang.patch \
            file://config.cache \
"
# commit id corresponds to the revision in package version
SRCREV = "a25949ff653ac5ae7a204381a3ebfd800feeaa01"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+(\+\d+)*)"
