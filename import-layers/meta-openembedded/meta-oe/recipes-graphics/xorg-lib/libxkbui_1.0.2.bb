require recipes-graphics/xorg-lib/xorg-lib-common.inc
SUMMARY = "X11 keyboard UI presentation library"
LICENSE = "GPL"
LIC_FILES_CHKSUM = "file://COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94"
DEPENDS += "virtual/libx11 libxt libxkbfile"
PE = "1"
PR = "r10"

SRC_URI[md5sum] = "1143e456f7429e18e88f2eadb2f2b6b1"
SRC_URI[sha256sum] = "20c23101d63234ee5f6d696dfa069b29c6c58e39eff433bcd7705b50b3ffa214"
