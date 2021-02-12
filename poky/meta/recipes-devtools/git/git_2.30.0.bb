require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "d24c4fa2a658318c2e66e25ab67cc30038a35696d2d39e6b12ceccf024de1e5e"
SRC_URI[manpages.sha256sum] = "e23035ae232c9a5eda57db258bc3b7f1c1060cfd66920f92c7d388b6439773a6"
