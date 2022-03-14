require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "fc4eb5ecb9299db91cdd156c06cdeb41833f53adc5631ddf8c0cb13eaa2911c1"
SRC_URI[manpages.sha256sum] = "220f1ed68582caeddf79c4db15e4eaa4808ec01fd11889e19232f0a74d7f31b0"
