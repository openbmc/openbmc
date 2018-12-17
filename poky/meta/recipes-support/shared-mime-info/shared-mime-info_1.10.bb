require shared-mime-info.inc

SRC_URI += "file://parallelmake.patch \
	    file://install-data-hook.patch"

SRC_URI[md5sum] = "418c2ced9dc4dd5ca8b06a755e6d64e9"
SRC_URI[sha256sum] = "c625a83b4838befc8cafcd54e3619946515d9e44d63d61c4adf7f5513ddfbebf"
