require libav.inc

SRC_URI[md5sum] = "75e838068a75fb88e1b4ea0546bc16f0"
SRC_URI[sha256sum] = "0875e835da683eef1a7bac75e1884634194149d7479d1538ba9fbe1614d066d7"

SRC_URI += "file://libav-fix-CVE-2014-9676.patch"
