require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "c64012d491e24c7d65cd389f75383d91"
SRC_URI[tarball.sha256sum] = "7104c4f5d948a75b499a954524cb281fe30c6649d8abe20982936f75ec1f275b"
SRC_URI[manpages.md5sum] = "58020dc13a5801c49f7986fef7027535"
SRC_URI[manpages.sha256sum] = "0dfe1931ad302873470e0280248e3aa50502b5edd00dcc3e9c9173667b6fea6a"
