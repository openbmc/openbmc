SUMMARY = "Panel plugin to display frequency of all cpus"
HOMEPAGE = "https://docs.xfce.org/panel-plugins/xfce4-cpufreq-plugin/start"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=1f6f1c0be32491a0c8d2915607a28f36"

XFCE_COMPRESS_TYPE = "xz"

inherit xfce-panel-plugin perlnative

SRC_URI[sha256sum] = "baa5b90f72e8c262777f1e246acae125af489e2c168a5f7f890d9d2b5567ec20"
