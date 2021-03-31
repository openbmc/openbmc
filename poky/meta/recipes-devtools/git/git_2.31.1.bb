require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "46d37c229e9d786510e0c53b60065704ce92d5aedc16f2c5111e3ed35093bfa7"
SRC_URI[manpages.sha256sum] = "d330498aaaea6928b0abbbbb896f6f605efd8d35f23cbbb2de38c87a737d4543"
