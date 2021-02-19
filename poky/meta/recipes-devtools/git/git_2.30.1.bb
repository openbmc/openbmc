require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "23a3e53f0d2dd3e62a8147b24a1a91d6ffe95b92123ef4dbae04e9a6205e71c0"
SRC_URI[manpages.sha256sum] = "db323e1b242e9d0337363b1e538c8b879e4c46eedbf94d3bee9e65dab6d49138"
