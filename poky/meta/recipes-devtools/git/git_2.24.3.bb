require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.sha256sum] = "ef6d1d1de1d7921a54d23d07479bd2766f050d6435cea5d3b5322aa4897cb3d7"
SRC_URI[manpages.sha256sum] = "325795ba33c0be02370de79636f32ad3b447665c1f2b5b4de65181fa804bed31"
