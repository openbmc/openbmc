require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "e1c17777528f55696815ef33587b1d20f5eec246669f3b839d15dbfffad9c121"
SRC_URI[manpages.sha256sum] = "b3c6cfc980f3c593d0cd0c63e0c97d6f1cafe7b72321fc3a94948758b9529c5b"

