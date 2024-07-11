require ncurses.inc

SRC_URI += "file://0001-tic-hang.patch \
           file://0002-configure-reproducible.patch \
           file://0003-gen-pkgconfig.in-Do-not-include-LDFLAGS-in-generated.patch \
           file://exit_prototype.patch \
           file://0001-Fix-CVE-2023-29491.patch \
           file://0001-Updating-reset-code-ncurses-6.4-patch-20231104.patch \
           file://CVE-2023-50495.patch \
           file://CVE-2023-45918.patch \
           "
# commit id corresponds to the revision in package version
SRCREV = "79b9071f2be20a24c7be031655a5638f6032f29f"
S = "${WORKDIR}/git"
EXTRA_OECONF += "--with-abi-version=5"
UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+(\.\d+)+)$"

# This is needed when using patchlevel versions like 6.1+20181013
#CVE_VERSION = "${@d.getVar("PV").split('+')[0]}.${@d.getVar("PV").split('+')[1]}"
