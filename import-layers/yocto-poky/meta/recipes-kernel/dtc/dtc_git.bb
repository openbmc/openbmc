require dtc.inc

LIC_FILES_CHKSUM = "file://GPL;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://libfdt/libfdt.h;beginline=3;endline=52;md5=fb360963151f8ec2d6c06b055bcbb68c"

SRCREV = "302fca9f4c283e1994cf0a5a9ce1cf43ca15e6d2"
PV = "1.4.1+git${SRCPV}"

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native nativesdk"
