require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "6deab33485c07cb3391ea0f255a936f2"
SRC_URI[tarball.sha256sum] = "a4b7e4365bee43caa12a38d646d2c93743d755d1cea5eab448ffb40906c9da0b"
SRC_URI[manpages.md5sum] = "d6cb42f12185a47ce3adaac24a1ded50"
SRC_URI[manpages.sha256sum] = "f6a5750dfc4a0aa5ec0c0cc495d4995d1f36ed47591c3941be9756c1c3a1aa0a"
