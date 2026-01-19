# Copyright (c) 2021-24 Axiado Corporation (or its affiliates). All rights reserved.

SUMMARY = "Sysproxy"
SECTION = "Sysproxy application"
LICENSE = "CLOSED"
PV = "1.0"

LIC_FILES_CHKSUM ?= "file://COPYING.axiado;md5=01d0d9bdb04606d39dcbff1ca352f133"

SRCBRANCH ?= "${LATEST_RELEASE_VERSION}"
SRCBRANCH_qpc ?= "release/release-7.1.0"
SRCREV_FORMAT = "sysproxy_axhal_sdk_porting_qpc"
SRCREV_sysproxy = "dd3543c5657d87bd1664c0469d4233fa91104452"
SRCREV_axhal = "1c65acf361283d6393429a608e86a99e2b258897"
SRCREV_sdk = "eb9708e10b0ddacc56eef4065593b383a2bd57e4"
SRCREV_porting = "5750943eb6d69135d51f236e40c08f258fe9f745"
SRCREV_qpc = "2f827e8203df2fc613f6ab852b0dd8d081f6a432"
SRC_URI = "git://git@sourcevault.axiadord:7999/apps/sysproxy.git;protocol=ssh;branch=${SRCBRANCH};name=sysproxy \
           git://git@sourcevault.axiadord:7999/axsw/ax_hal.git;protocol=ssh;branch=${SRCBRANCH};name=axhal;destsuffix=ax_hal \
           git://git@sourcevault.axiadord:7999/axsw/sdk.git;protocol=ssh;branch=${SRCBRANCH};name=sdk;destsuffix=sdk \
           git://git@sourcevault.axiadord:7999/axsw/porting.git;protocol=ssh;branch=${SRCBRANCH};name=porting;destsuffix=porting \
           git://git@sourcevault.axiadord:7999/ext/qpc.git;protocol=ssh;branch=${SRCBRANCH_qpc};name=qpc;destsuffix=qpc \
           "

REL_BUILD_ID = "${@int(os.getenv('REL_BUILD_ID', '0'))}"

inherit obmc-phosphor-systemd cmake pkgconfig

S = "${WORKDIR}/git"

ERROR_QA:remove = "buildpaths"
WARNNING_QA:append = "buildpaths"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"
SYSTEMD_SERVICE:${PN} = "sysproxy.service"

do_configure:prepend() {
        cp -r ${UNPACKDIR}/sdk ${WORKDIR}/
        cp -r ${UNPACKDIR}/ax_hal ${WORKDIR}/
        cp -r ${UNPACKDIR}/qpc ${WORKDIR}/
        cp -r ${UNPACKDIR}/porting ${WORKDIR}/
}

do_compile() {
        rm -rf ${S}/build ${WORKDIR}/sdk/build ${WORKDIR}/ax_hal/build
        cd ${B}
        cmake ${S} -DREL_BUILD_ID=${REL_BUILD_ID} -DCMAKE_TRY_COMPILE_TARGET_TYPE=STATIC_LIBRARY
        cd ${S}
        cmake --build ${B}
}

do_install() {
        install -d ${D}/${systemd_unitdir}/system
        install -m 0644 ${S}/sysproxy.service ${D}/${systemd_unitdir}/system
        install -d ${D}${bindir}
        install -m 0755 ${B}/sysmgr_proxy ${D}${bindir}
}
