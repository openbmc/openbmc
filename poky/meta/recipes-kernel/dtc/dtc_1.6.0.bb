require dtc.inc

LIC_FILES_CHKSUM = "file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
		    file://libfdt/libfdt.h;beginline=4;endline=7;md5=05bb357cfb75cae7d2b01d2ee8d76407"

SRCREV = "2525da3dba9beceb96651dc2986581871dbeca30"

SRC_URI += "file://0001-fdtdump-Fix-gcc11-warning.patch"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
