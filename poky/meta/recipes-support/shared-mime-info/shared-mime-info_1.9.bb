require shared-mime-info.inc

SRC_URI += "file://parallelmake.patch \
	    file://install-data-hook.patch"

SRC_URI[md5sum] = "45103889b91242850aa47f09325e798b"
SRC_URI[sha256sum] = "5c0133ec4e228e41bdf52f726d271a2d821499c2ab97afd3aa3d6cf43efcdc83"
