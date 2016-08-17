# live555 OE build file
# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION = "LIVE555 Streaming Media libraries"
HOMEPAGE = "http://live.com/"
LICENSE = "LGPLv3"
SECTION = "devel"

INC_PR = "r1"

URLV = "${@d.getVar('PV',1)[0:4]}.${@d.getVar('PV',1)[4:6]}.${@d.getVar('PV',1)[6:8]}"
SRC_URI = "http://www.live555.com/liveMedia/public/live.${URLV}.tar.gz \
           file://config.linux-cross"
# only latest live version stays on http://www.live555.com/liveMedia/public/, add mirror for older
MIRRORS += "http://www.live555.com/liveMedia/public/ http://distcache.FreeBSD.org/ports-distfiles/ \n"

S = "${WORKDIR}/live"
TARGET_CC_ARCH += "${LDFLAGS}"

do_configure() {
    cp ${WORKDIR}/config.linux-cross .
    echo "COMPILE_OPTS+=" -fPIC "" >> config.linux-cross
    ./genMakefiles linux-cross
}

do_compile() {
    make
}

do_install() {
    install -d ${D}${includedir}/BasicUsageEnvironment
    install -d ${D}${includedir}/groupsock
    install -d ${D}${includedir}/liveMedia
    install -d ${D}${includedir}/UsageEnvironment
    install -d ${D}${libdir}
    cp -R --no-dereference --preserve=mode,links -v ${S}/BasicUsageEnvironment/include/*.hh ${D}${includedir}/BasicUsageEnvironment/
    cp -R --no-dereference --preserve=mode,links -v ${S}/groupsock/include/*.h ${D}${includedir}/groupsock/
    cp -R --no-dereference --preserve=mode,links -v ${S}/groupsock/include/*.hh ${D}${includedir}/groupsock/
    cp -R --no-dereference --preserve=mode,links -v ${S}/liveMedia/include/*.hh ${D}${includedir}/liveMedia/
    cp -R --no-dereference --preserve=mode,links -v ${S}/UsageEnvironment/include/*.hh ${D}${includedir}/UsageEnvironment/
    # Find all the headers
    for i in $(find . -name "*.hh") $(find . -name "*.h") ; do
        install ${i} ${D}${includedir}
    done
    cp ${S}/*/*.a ${D}${libdir}
    install -d ${D}${bindir}
    for i in MPEG2TransportStreamIndexer openRTSP playSIP sapWatch testMPEG1or2AudioVideoToDarwin testMPEG1or2ProgramToTransportStream testMPEG1or2Splitter testMPEG1or2VideoReceiver testMPEG2TransportStreamTrickPlay testMPEG4VideoToDarwin testOnDemandRTSPServer testRelay testAMRAudioStreamer testDVVideoStreamer testMP3Receiver testMP3Streamer testMPEG1or2AudioVideoStreamer testMPEG1or2VideoStreamer testMPEG2TransportStreamer testMPEG4VideoStreamer testWAVAudioStreamer vobStreamer; do
        install -m 0755 ${S}/testProgs/${i} ${D}${bindir}/
    done
    install -m 0755 ${S}/mediaServer/live555MediaServer ${D}${bindir}/
}

PACKAGES =+ " live555-openrtsp live555-playsip live555-mediaserver"
FILES_${PN} = "${bindir}/sapWatch ${bindir}/testMPEG1or2AudioVideoToDarwin ${bindir}/testMPEG1or2ProgramToTransportStream ${bindir}/testMPEG1or2Splitter ${bindir}/testMPEG1or2VideoReceiver ${bindir}/testMPEG2TransportStreamTrickPlay ${bindir}/testMPEG4VideoToDarwin ${bindir}/testOnDemandRTSPServer ${bindir}/testRelay ${bindir}/testAMRAudioStreamer ${bindir}/testDVVideoStreamer ${bindir}/testMP3Receiver ${bindir}/testMP3Streamer ${bindir}/testMPEG1or2AudioVideoStreamer ${bindir}/testMPEG1or2VideoStreamer ${bindir}/testMPEG2TransportStreamer ${bindir}/testMPEG4VideoStreamer ${bindir}/testWAVAudioStreamer ${bindir}/vobStreamer ${bindir}/MPEG2TransportStreamIndexer"
FILES_live555-openrtsp = "${bindir}/openRTSP"
FILES_live555-playsip = "${bindir}/playSIP"
FILES_live555-mediaserver = "${bindir}/live555MediaServer"
