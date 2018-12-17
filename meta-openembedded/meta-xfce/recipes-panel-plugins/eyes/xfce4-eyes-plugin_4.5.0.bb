SUMMARY = "Panel plugin with graphical representation of the cpu frequency"
HOMEPAGE = "http://goodies.xfce.org/projects/panel-plugins/xfce4-eyes-plugin"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit xfce-panel-plugin

SRC_URI[md5sum] = "6e274ceda37c7a8ae0821d9d49e965e9"
SRC_URI[sha256sum] = "fdae00036383105a15d12e9809abd5945a8f2152b17e16ccdfbfe5bd9733f29d"

FILES_${PN} += "${datadir}/xfce4/eyes"
