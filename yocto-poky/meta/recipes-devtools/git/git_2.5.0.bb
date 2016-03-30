require git.inc

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "
EXTRA_OEMAKE += "NO_GETTEXT=1"

SRC_URI[tarball.md5sum] = "3bc9b0a803ae8ec6c5316cc64f0b7f78"
SRC_URI[tarball.sha256sum] = "8fa13ba8434ff83d24f57f831d55dbb9046434c266641180a37744facfce72ac"
SRC_URI[manpages.md5sum] = "134b049e51420a336049aac21c88a75a"
SRC_URI[manpages.sha256sum] = "745e4e797fe5061e781c880d370b1beb480199127da5acaf4e376e0b09d4d685"

SRC_URI += "\
    file://0008-CVE-2015-7545-1.patch \
    file://0009-CVE-2015-7545-2.patch \
    file://0010-CVE-2015-7545-3.patch \
    file://0011-CVE-2015-7545-4.patch \
    file://0012-CVE-2015-7545-5.patch \
    "
