require ncurses.inc

SRC_URI += "file://0001-tic-hang.patch \
            file://0002-configure-reproducible.patch \
            file://config.cache \
            file://CVE-2017-13732-CVE-2017-13734-CVE-2017-13730-CVE-2017-13729-CVE-2017-13728-CVE-2017-13731.patch \
"
# commit id corresponds to the revision in package version
SRCREV = "52681a6a1a18b4d6eb1a716512d0dd827bd71c87"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+(\+\d+)*)"
