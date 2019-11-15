require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "ed39361a3ae362c8af852d1a06992bc2"
SRC_URI[tarball.sha256sum] = "ad11030d2eac656ee9e8862f56d1610550f7867181beff814c7712a99192e99d"
SRC_URI[manpages.md5sum] = "57465e83f13ba910a178b717d93958c0"
SRC_URI[manpages.sha256sum] = "ce995f86f441b56ab1fd0788a94786904ae2e2989e7191fd68060003011366d7"
