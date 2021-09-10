require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "02d909d0bba560d3a1008bd00dd577621ffb57401b09175fab2bf6da0e9704ae"
SRC_URI[manpages.sha256sum] = "ba9cd0f29a3632a3b78f8ed2389f0780aa6e8fcbe258259d7c584920d19ed1f7"
