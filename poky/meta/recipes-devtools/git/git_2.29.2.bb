require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "869a121e1d75e4c28213df03d204156a17f02fce2dc77be9795b327830f54195"
SRC_URI[manpages.sha256sum] = "68b258e6d590cb78e02c0df741bbaeab94cbbac6d25de9da4fb3882ee098307b"
