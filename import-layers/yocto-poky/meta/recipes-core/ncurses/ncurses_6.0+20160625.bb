require ncurses.inc

SRC_URI += "file://tic-hang.patch \
            file://config.cache \
"
# commit id corresponds to the revision in package version
SRCREV = "63dd558cb8e888d6fab5f00bbf7842736a2356b9"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+(\+\d+)*)"
