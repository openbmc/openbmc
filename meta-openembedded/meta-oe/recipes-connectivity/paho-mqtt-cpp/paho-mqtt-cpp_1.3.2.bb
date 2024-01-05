SUMMARY = "Paho MQTT - C++ libraries for the MQTT and MQTT-SN protocols"
DESCRIPTION = "Client implementation of open and standard messaging protocols for Machine-to-Machine (M2M) and Internet of Things (IoT)."
HOMEPAGE = "http://www.eclipse.org/paho/"
SECTION = "console/network"
LICENSE = "EPL-2.0 | EDL-1.0"

LIC_FILES_CHKSUM = " \
    file://src/mqtt/message.h;beginline=9;endline=18;md5=c5ceecf5ab99d44dcfaaabdce289071b \
    file://edl-v10;md5=3adfcc70f5aeb7a44f3f9b495aa1fbf3 \
    file://epl-v20;md5=d9fc0efef5228704e7f5b37f27192723 \
"

SRC_URI = "git://github.com/eclipse/paho.mqtt.cpp;protocol=https;branch=v1.3.x \
           file://0001-cmake-Use-CMAKE_INSTALL_LIBDIR-and-CMAKE_INSTALL_BIN.patch \
           "
SRCREV = "5e0d1bf37b4826d680ec066ec42afd133851a681"

DEPENDS = "openssl paho-mqtt-c"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DPAHO_WITH_SSL=ON"
