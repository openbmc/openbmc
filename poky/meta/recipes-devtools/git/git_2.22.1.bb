require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "b379ee9b8c7733d01fc49998148621ef"
SRC_URI[tarball.sha256sum] = "83bf264bfa4573faddb396cbc7f5518db082f9573aa3c8ea08b8a30d23a46adc"
SRC_URI[manpages.md5sum] = "70aaf2da41c21b0864e9b1bb8231172c"
SRC_URI[manpages.sha256sum] = "228da34377a6795619e1822a4fad00c87300135b811643bdb816afe4a92040f9"
