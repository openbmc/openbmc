require ncurses.inc

SRC_URI += "file://0001-tic-hang.patch \
           file://0002-configure-reproducible.patch \
           file://0003-gen-pkgconfig.in-Do-not-include-LDFLAGS-in-generated.patch \
           file://exit_prototype.patch \
           file://0001-do-not-create-symlink-to-terminfo-under-usr-lib.patch \
           "
# commit id corresponds to the revision in package version
SRCREV = "a1c9c082bbe6ac18d96eb2e1ee2146e1665deaf8"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+_\d+)$"

# This is needed when using patchlevel versions like 6.1+20181013
#CVE_VERSION = "${@d.getVar("PV").split('+')[0]}.${@d.getVar("PV").split('+')[1]}"
