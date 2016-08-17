require libbonobo.inc

SRC_URI += "file://0001-Remove-use-of-G_DISABLE_DEPRECATED.patch \
            file://do-not-use-srcdir-variable.patch \
           "
SRC_URI[archive.md5sum] = "27fa902d4fdf6762ee010e7053aaf77b"
SRC_URI[archive.sha256sum] = "9160d4f277646400d3bb6b4fa73636cc6d1a865a32b9d0760e1e9e6ee624976b"
GNOME_COMPRESS_TYPE="bz2"
