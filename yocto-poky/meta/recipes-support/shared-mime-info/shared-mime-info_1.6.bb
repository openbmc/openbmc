require shared-mime-info.inc

SRC_URI += "file://parallelmake.patch \
	    file://install-data-hook.patch"

SRC_URI[md5sum] = "10abc5c4e6b22223ff05c3bd70ff9e8f"
SRC_URI[sha256sum] = "b2f8f85b6467933824180d0252bbcaee523f550a8fbc95cc4391bd43c03bc34c"
