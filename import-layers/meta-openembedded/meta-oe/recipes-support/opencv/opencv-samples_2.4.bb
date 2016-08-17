SUMMARY = "Opencv : The Open Computer Vision Library"
HOMEPAGE = "http://opencv.willowgarage.com/wiki/"
SECTION = "libs"
LICENSE = "BSD"

DEPENDS = "opencv"

LIC_FILES_CHKSUM = "file://include/opencv2/opencv.hpp;endline=41;md5=6d690d8488a6fca7a2c192932466bb14 \
"
SRCREV = "6fae07ba8867b8fd2c53344a774aab669afa7c5e"
SRC_URI = "git://github.com/Itseez/opencv.git;branch=2.4 \
          "
PV = "2.4.3+git${SRCPV}"

S = "${WORKDIR}/git"

do_install() {
    cd samples/c
    install -d ${D}/${bindir}
    install -d ${D}/${datadir}/opencv/samples

    cp * ${D}/${datadir}/opencv/samples || true

    for i in *.c; do
        echo "compiling $i"
        ${CXX} ${CFLAGS} ${LDFLAGS} -ggdb `pkg-config --cflags opencv` -o `basename $i .c` $i `pkg-config --libs opencv` || true
        install -m 0755 `basename $i .c` ${D}/${bindir} || true
        rm ${D}/${datadir}/opencv/samples/`basename $i .c` || true
    done
    for i in *.cpp; do
        echo "compiling $i"
        ${CXX} ${CFLAGS} ${LDFLAGS} -ggdb `pkg-config --cflags opencv` -o `basename $i .cpp` $i `pkg-config --libs opencv` || true
        install -m 0755 `basename $i .cpp` ${D}/${bindir} || true
        rm ${D}/${datadir}/opencv/samples/`basename $i .cpp` || true
    done
}

FILES_${PN}-dev += "${datadir}/opencv/samples/*.c* ${datadir}/opencv/samples/*.vcp* ${datadir}/opencv/samples/build*" 
FILES_${PN} += "${bindir} ${datadir}/opencv"

PNBLACKLIST[opencv-samples] ?= "Depends on blacklisted opencv"
