SUMMARY = "Easy to use task manager"
HOMEPAGE = "https://docs.xfce.org/apps/xfce4-taskmanager/start"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

XFCE_COMPRESS_TYPE = "xz"
XFCEBASEBUILDCLASS = "meson"

inherit xfce-app

DEPENDS += "gtk+3 cairo libwnck libxfce4ui libxmu"

SRC_URI[sha256sum] = "29bdc7840ab8b9025f6c0e456a83a31090d1c9fd9e26b359baa4a4010cfb0b90"
