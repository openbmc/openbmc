require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "7a7769e5c957364ed0aed89e6e67c254"
SRC_URI[tarball.sha256sum] = "edc3bc1495b69179ba4e272e97eff93334a20decb1d8db6ec3c19c16417738fd"
SRC_URI[manpages.md5sum] = "78c6e54a61a167dab5e8ae07036293ab"
SRC_URI[manpages.sha256sum] = "e9c123463abd05e142defe44a8060ce6e9853dfd8c83b2542e38b7deac4e6d4c"
