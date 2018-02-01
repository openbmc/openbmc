SECTION = "libs"
require libidl_${PV}.bb
inherit native

PR = "r1"

DEPENDS = "bison-native glib-2.0-native flex-native"

SRC_URI[md5sum] = "bb8e10a218fac793a52d404d14adedcb"
SRC_URI[sha256sum] = "c5d24d8c096546353fbc7cedf208392d5a02afe9d56ebcc1cccb258d7c4d2220"
