require sysstat.inc

LIC_FILES_CHKSUM = "file://COPYING;md5=a23a74b3f4caf9616230789d94217acb"

SRC_URI += "file://0001-Include-needed-headers-explicitly.patch \
	    file://0001-Fix-232-Memory-corruption-bug-due-to-Integer-Overflo.patch \
"

SRC_URI[md5sum] = "d8e3bbb9c873dd370f6d33664e326570"
SRC_URI[sha256sum] = "f752f3c406153a6fc446496f1102872505ace3f0931d975c1d664c81ec09f129"
