# live555 OE build file
# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION = "LIVE555 Streaming Media libraries"
HOMEPAGE = "http://live.com/"
LICENSE = "LGPL-3.0-only"
SECTION = "devel"

DEPENDS = "openssl"

URLV = "${@d.getVar('PV')[0:4]}.${@d.getVar('PV')[4:6]}.${@d.getVar('PV')[6:8]}"
SRC_URI = "https://download.videolan.org/pub/contrib/live555/live.${URLV}.tar.gz \
           file://config.linux-cross"

# only latest live version stays on http://www.live555.com/liveMedia/public/, add mirror for older
MIRRORS += "http://www.live555.com/liveMedia/public/ http://download.videolan.org/contrib/live555/ \n"

SRC_URI[sha256sum] = "ce95a1c79f6d18e959f9dc129b8529b711c60e76754acc285e60946303b923ec"

S = "${WORKDIR}/live"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                   "

TARGET_CC_ARCH += "${LDFLAGS}"

do_configure() {
    cp ${UNPACKDIR}/config.linux-cross .
    echo "COMPILE_OPTS+=" -fPIC -DXLOCALE_NOT_USED"" >> config.linux-cross
    ./genMakefiles linux-cross
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
    for i in MPEG2TransportStreamIndexer openRTSP playSIP sapWatch testMPEG1or2ProgramToTransportStream testMPEG1or2Splitter testMPEG1or2VideoReceiver testMPEG2TransportStreamTrickPlay testOnDemandRTSPServer testRelay testAMRAudioStreamer testDVVideoStreamer testMP3Receiver testMP3Streamer testMPEG1or2AudioVideoStreamer testMPEG1or2VideoStreamer testMPEG2TransportStreamer testMPEG4VideoStreamer testWAVAudioStreamer vobStreamer; do
        install -m 0755 ${S}/testProgs/${i} ${D}${bindir}/
    done
    install -m 0755 ${S}/mediaServer/live555MediaServer ${D}${bindir}/
}

RDEPENDS:${PN}-dev = ""
PACKAGES =+ "live555-openrtsp live555-playsip live555-mediaserver live555-examples"
FILES:live555-openrtsp = "${bindir}/openRTSP"
FILES:live555-playsip = "${bindir}/playSIP"
FILES:live555-mediaserver = "${bindir}/live555MediaServer"
FILES:live555-examples = "${bindir}/*"
