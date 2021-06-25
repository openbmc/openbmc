require dtc.inc

LIC_FILES_CHKSUM = "file://GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
		    file://libfdt/libfdt.h;beginline=4;endline=7;md5=05bb357cfb75cae7d2b01d2ee8d76407"

SRCREV = "b6910bec11614980a21e46fbccc35934b671bd81"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
