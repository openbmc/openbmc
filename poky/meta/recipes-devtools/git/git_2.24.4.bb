require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "6e119e70d3762f28e1dc9928c526eb4d7519fd3870f862775cd10186653eb85a"
SRC_URI[manpages.sha256sum] = "e687bcc91a6fd9cb74243f91a9c2d77c50ce202a09b35931021ecc521a373ed5"
