SUMMARY = "Enumerate all known properties of OpenCL platform and devices."
DESCRIPTION = "clinfo is a simple command-line application that enumerates \
all possible (known) properties of the OpenCL platform and devices \
available on the system."
HOMEPAGE = "https://github.com/Oblomov/clinfo"

LICENSE = "CC0-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd8857f774dfb0eefe1e80c8f9240a7e"

SRC_URI = "git://github.com/Oblomov/clinfo.git;protocol=https;branch=master"

SRCREV = "59d0daf898e48d76ccbb788acbba258fa0a8ba7c"

S = "${WORKDIR}/git"

DEPENDS += "opencl-headers opencl-icd-loader"

do_install() {
        install -D -m 755 clinfo ${D}${bindir}/clinfo
}
