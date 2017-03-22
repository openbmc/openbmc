require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "c783361be894b8bfa5373811b1b65602"
SRC_URI[tarball.sha256sum] = "a252b6636b12d5ba57732c8469701544c26c2b1689933bd1b425e603cbb247c0"
SRC_URI[manpages.md5sum] = "66fafd61d65f9d2d99581133170eb186"
SRC_URI[manpages.sha256sum] = "8ea1a55b048fafbf0c0c6fcbca4b5b0f5e9917893221fc7345c09051d65832ce"
