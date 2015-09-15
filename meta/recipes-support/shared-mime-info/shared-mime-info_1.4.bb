require shared-mime-info.inc

SRC_URI += "file://parallelmake.patch \
	    file://install-data-hook.patch"

SRC_URI[md5sum] = "16c02f7b658fff2a9c207406d388ea31"
SRC_URI[sha256sum] = "bbc0bd023f497dfd75e1ca73441cbbb5a63617d9e14f2790b868361cc055b5b1"
