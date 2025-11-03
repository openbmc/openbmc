require ncurses.inc

SRC_URI += "file://0001-tic-hang.patch \
           file://0002-configure-reproducible.patch \
           file://0003-gen-pkgconfig.in-Do-not-include-LDFLAGS-in-generated.patch \
           file://exit_prototype.patch \
           file://0001-Fix-CVE-2023-29491.patch \
           file://0001-Updating-reset-code-ncurses-6.4-patch-20231104.patch \
           file://CVE-2023-50495.patch \
           file://CVE-2023-45918.patch \
           file://CVE-2025-6141.patch \
           "
# commit id corresponds to the revision in package version
SRCREV = "1003914e200fd622a27237abca155ce6bf2e6030"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+_\d+)$"

# This is needed when using patchlevel versions like 6.1+20181013
#CVE_VERSION = "${@d.getVar("PV").split('+')[0]}.${@d.getVar("PV").split('+')[1]}"
