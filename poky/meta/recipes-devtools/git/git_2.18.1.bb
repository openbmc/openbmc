require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "9b62c267d878f6cb02f8abc59a99525d"
SRC_URI[tarball.sha256sum] = "5c710c866d8c9ba3b3e062755e0e9d0ef4f665752bd64810e3eb9f1b0f0eb076"
SRC_URI[manpages.md5sum] = "ef32a459a4a08a3b8e837a31c925c848"
SRC_URI[manpages.sha256sum] = "d05bfab2dc45de4f6e7d61ca173071d6902905a4963f7ac3cbca608c0d4592c9"
