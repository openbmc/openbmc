require shared-mime-info.inc

SRC_URI += "file://parallelmake.patch \
	    file://install-data-hook.patch"

SRC_URI[md5sum] = "f6dcadce764605552fc956563efa058c"
SRC_URI[sha256sum] = "2af55ef1a0319805b74ab40d331a3962c905477d76c086f49e34dc96363589e9"
