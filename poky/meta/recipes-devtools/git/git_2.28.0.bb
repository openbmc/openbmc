require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "f914c60a874d466c1e18467c864a910dd4ea22281ba6d4d58077cb0c3f115170"
SRC_URI[manpages.sha256sum] = "3cfca28a88d5b8112ea42322b797a500a14d0acddea391aed0462aff1ab11bf7"
