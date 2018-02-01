require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "6a7a73db076bb0514b602720669d685c"
SRC_URI[tarball.sha256sum] = "a1cdd7c820f92c44abb5003b36dc8cb7201ba38e8744802399f59c97285ca043"
SRC_URI[manpages.md5sum] = "e4268a6b514ccdb624b6450ff55881a3"
SRC_URI[manpages.sha256sum] = "ee567e7b0f95333816793714bb31c54e288cf8041f77a0092b85e62c9c2974f9"
