require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "b8e00c2997774c5d4aaf26fd9d9aaf64"
SRC_URI[tarball.sha256sum] = "85eca51c7404da75e353eba587f87fea9481ba41e162206a6f70ad8118147bee"
SRC_URI[manpages.md5sum] = "8a168697b99a9a3f04f29f7d4bacd70b"
SRC_URI[manpages.sha256sum] = "14c76ebb4e31f9e55cf5338a04fd3a13bced0323cd51794ccf45fc74bd0c1080"
