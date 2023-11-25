SUMMARY = "Paho MQTT - C libraries for the MQTT and MQTT-SN protocols"
DESCRIPTION = "Client implementation of open and standard messaging protocols for Machine-to-Machine (M2M) and Internet of Things (IoT)."
HOMEPAGE = "http://www.eclipse.org/paho/"
SECTION = "console/network"
LICENSE = "EPL-2.0 | EDL-1.0"

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=fd3b896dadaeec3410d753ffaeadcfac \
    file://edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3 \
    file://epl-v20;md5=d9fc0efef5228704e7f5b37f27192723 \
"

SRC_URI = "git://github.com/eclipse/paho.mqtt.c;protocol=https;branch=master"

SRCREV = "07a875788d8cc6f5833b12581d6e3e349b34d719"

DEPENDS = "openssl"

S = "${WORKDIR}/git"

inherit cmake

do_configure:prepend() {
    sed -i s:\ lib/cmake:\ ${baselib}/cmake:g ${S}/src/CMakeLists.txt
}

do_install:append() {
    # paho-mqtt installes some thing that we don't want.
    rm -rf ${D}${prefix}/samples
    find ${D}${prefix} -maxdepth 1 -type f -delete
}

EXTRA_OECMAKE = "-DPAHO_WITH_SSL=ON -DPAHO_ENABLE_TESTING=OFF -DPAHO_HIGH_PERFORMANCE=ON"
