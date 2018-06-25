require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "37467da8e79e72f28598d667f219f75e"
SRC_URI[tarball.sha256sum] = "56cfa48af2b289bba172ca0a47c29f0083f5846cf4759978b70988e4f07fc9fd"
SRC_URI[manpages.md5sum] = "5587407f3c28446af12fde3f3131ba34"
SRC_URI[manpages.sha256sum] = "d499e825f429d76862be415f579c20cc26b046573a3a39237acaf9682cb71be7"
