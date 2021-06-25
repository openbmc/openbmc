require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "6038f06d396ba9dab2eee541c7db6e7f9f847f181ec62f3d8441893f8c469398"
SRC_URI[manpages.sha256sum] = "b5533c40ea1688231c0e2df51cc0d1c0272e17fe78a45ba6e60cb8f61fa4a53c"
