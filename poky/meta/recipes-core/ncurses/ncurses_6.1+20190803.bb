require ncurses.inc

SRC_URI += "file://0001-tic-hang.patch \
            file://0002-configure-reproducible.patch \
            file://config.cache \
"
# commit id corresponds to the revision in package version
SRCREV = "3c9b2677c96c645496997321bf2fe465a5e7e21f"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5 --cache-file=${B}/config.cache"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+(\+\d+)*)"
