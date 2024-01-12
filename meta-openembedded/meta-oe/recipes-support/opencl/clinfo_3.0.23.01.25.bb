SUMMARY = "Enumerate all known properties of OpenCL platform and devices."
DESCRIPTION = "clinfo is a simple command-line application that enumerates \
all possible (known) properties of the OpenCL platform and devices \
available on the system."
HOMEPAGE = "https://github.com/Oblomov/clinfo"

LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd8857f774dfb0eefe1e80c8f9240a7e"

SRC_URI = "git://github.com/Oblomov/clinfo.git;protocol=https;branch=master"

SRCREV = "748c3930a9b9cb826e631d77439e2cb8f84f5bcf"

S = "${WORKDIR}/git"

DEPENDS += "opencl-headers virtual/opencl-icd"

do_install() {
	oe_runmake install PREFIX=${D}${prefix} MANDIR=${D}${mandir}
}
