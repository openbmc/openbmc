require ncurses.inc

SRC_URI += "file://0001-tic-hang.patch \
            file://0002-configure-reproducible.patch \
            file://config.cache \
"
# commit id corresponds to the revision in package version
SRCREV = "7a97a7f937762ba342d5b2fd7cd090885a809835"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5 --cache-file=${B}/config.cache"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+(\+\d+)*)"
