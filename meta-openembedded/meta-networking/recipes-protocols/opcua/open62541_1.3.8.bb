DESCRIPTION = "open62541 is an implementation of OPC UA (OPC Unified Architecture)"
HOMEPAGE = "https://github.com/open62541/open62541.git"
LICENSE = "MPL-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad \
    file://deps/mdnsd/LICENSE;md5=3bb4047dc4095cd7336de3e2a9be94f0 \
    file://deps/mqtt-c/LICENSE;md5=9226377baf0b79174c89a1ab55592456 \
"

SRCREV_FORMAT = "opcua_mdnsd_ua-nodeset_mqtt-c"
SRCREV_opcua = "086b1557d6f49e8a82c999054a7da77d080cd54e"
SRCREV_mdnsd = "3151afe5899dba5125dffa9f4cf3ae1fe2edc0f0"
SRCREV_ua-nodeset = "f71b3f411d5cb16097c3ae0c744f67ad45535ffb"
SRCREV_mqtt-c = "f69ce1e7fd54f3b1834c9c9137ce0ec5d703cb4d"

SRC_URI = " \
    git://github.com/open62541/open62541.git;name=opcua;branch=1.3;protocol=https \
    git://github.com/Pro/mdnsd.git;name=mdnsd;protocol=https;branch=master;destsuffix=git/deps/mdnsd \
    git://github.com/OPCFoundation/UA-Nodeset;name=ua-nodeset;protocol=https;branch=v1.04;destsuffix=git/deps/ua-nodeset \
    git://github.com/LiamBindle/MQTT-C.git;name=mqtt-c;protocol=https;branch=master;destsuffix=git/deps/mqtt-c \
    file://0001-fix-build-do-not-install-git-files.patch \
"

S = "${WORKDIR}/git"

inherit cmake python3native

EXTRA_OECMAKE += "\
    -DBUILD_SHARED_LIBS=ON \
    -DUA_NAMESPACE_ZERO=FULL \
    -DUA_LOGLEVEL=600 \
    -DCMAKE_BUILD_TYPE=RelWithDebInfo \
"

PACKAGECONFIG ?= "encryption-mbedtls pubsub pubsub-eth subscriptions subscriptions-events"
PACKAGECONFIG[amalgamation] = "-DUA_ENABLE_AMALGAMATION=ON, -DUA_ENABLE_AMALGAMATION=OFF"
PACKAGECONFIG[encryption-mbedtls] = "-DUA_ENABLE_ENCRYPTION=MBEDTLS, , mbedtls, , , encryption-openssl"
PACKAGECONFIG[encryption-openssl] = "-DUA_ENABLE_ENCRYPTION=OPENSSL, , openssl, , , encryption-mbedtls"
PACKAGECONFIG[multithreading] = "-DUA_MULTITHREADING=100, -DUA_MULTITHREADING=0"
PACKAGECONFIG[pubsub] = "-DUA_ENABLE_PUBSUB=ON, -DUA_ENABLE_PUBSUB=OFF"
PACKAGECONFIG[pubsub-eth] = "-DUA_ENABLE_PUBSUB_ETH_UADP=ON, -DUA_ENABLE_PUBSUB_ETH_UADP=OFF"
PACKAGECONFIG[subscriptions] = "-DUA_ENABLE_SUBSCRIPTIONS=ON, -DUA_ENABLE_SUBSCRIPTIONS=OFF"
PACKAGECONFIG[subscriptions-events] = "-DUA_ENABLE_SUBSCRIPTIONS_EVENTS=ON, -DUA_ENABLE_SUBSCRIPTIONS_EVENTS=OFF"
PACKAGECONFIG[werror] = "-DUA_FORCE_WERROR=ON, -DUA_FORCE_WERROR=OFF"

do_configure:prepend:toolchain-clang:riscv64() {
    sed -i -e 's/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION ON)/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION OFF)/' ${S}/CMakeLists.txt
}

do_configure:prepend:toolchain-clang:riscv32() {
    sed -i -e 's/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION ON)/set(CMAKE_INTERPROCEDURAL_OPTIMIZATION OFF)/' ${S}/CMakeLists.txt
}

