require ncurses.inc

SRC_URI += "file://tic-hang.patch \
            file://fix-cflags-mangle.patch \
            file://config.cache \
            file://configure-reproducible.patch \
"
# commit id corresponds to the revision in package version
SRCREV = "3db0bd19cb50e3d9b4f2cf15b7a102fe11302068"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+(\+\d+)*)"
